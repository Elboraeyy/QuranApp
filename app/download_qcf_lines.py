"""
Download word-level QCF V1 data with line numbers from Quran.com API.
This creates a line-by-line mapping so each Mushaf line is rendered exactly.
Output: qcf_lines.json -> { "pages": { "1": { "1": "code_v1...", "2": "..." }, ... } }
"""
import urllib.request
import json
import os
import time

assets_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "src", "main", "assets")
output_file = os.path.join(assets_dir, "qcf_lines.json")

# Resume support: load existing data if available
if os.path.exists(output_file):
    with open(output_file, "r", encoding="utf-8") as f:
        all_pages = json.load(f)
else:
    all_pages = {}

print("Downloading word-level QCF V1 data with line numbers...")

for page in range(1, 605):
    page_key = str(page)
    if page_key in all_pages and len(all_pages[page_key]) > 0:
        if page % 50 == 0:
            print(f"  Page {page}/604 already cached.")
        continue

    url = f"https://api.quran.com/api/v4/verses/by_page/{page}?language=ar&words=true&word_fields=code_v1,line_number&per_page=50"
    
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req, timeout=15) as resp:
            data = json.loads(resp.read().decode("utf-8"))
        
        lines = {}  # line_number -> combined code_v1 string
        
        for verse in data.get("verses", []):
            for word in verse.get("words", []):
                line_num = str(word.get("line_number", 1))
                code = word.get("code_v1", "")
                if code:
                    if line_num not in lines:
                        lines[line_num] = ""
                    lines[line_num] += code + " "
        
        # Trim trailing spaces
        for k in lines:
            lines[k] = lines[k].strip()
        
        all_pages[page_key] = lines
        
    except Exception as e:
        print(f"  ERROR page {page}: {e}")
        all_pages[page_key] = {}
    
    if page % 20 == 0:
        print(f"  Downloaded {page}/604 pages...")
        # Save periodically
        with open(output_file, "w", encoding="utf-8") as f:
            json.dump(all_pages, f, ensure_ascii=False)
    
    time.sleep(0.15)  # Rate limit

# Final save
with open(output_file, "w", encoding="utf-8") as f:
    json.dump(all_pages, f, ensure_ascii=False)

print(f"Done! Saved to {output_file}")
print(f"Total pages: {len(all_pages)}")
