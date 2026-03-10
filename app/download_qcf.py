import urllib.request
import os
import json
import concurrent.futures

assets_dir = r"e:\Dev\android_app\quranapp\app\src\main\assets"
fonts_dir = os.path.join(assets_dir, "fonts")
os.makedirs(fonts_dir, exist_ok=True)

# 1. Download QCF V1 JSON database
# We get quran/verses/code_v1 from quran.com API which contains the QCF v1 characters for every ayah
print("Downloading QCF V1 text mapping...")
api_url = "https://api.quran.com/api/v4/quran/verses/code_v1"
req = urllib.request.Request(api_url, headers={'User-Agent': 'Mozilla/5.0'})
try:
    with urllib.request.urlopen(req) as resp:
        data = resp.read()
        parsed = json.loads(data)
        
        # Save to assets
        out_path = os.path.join(assets_dir, "qcf_quran.json")
        with open(out_path, "w", encoding="utf-8") as f:
            json.dump(parsed, f, ensure_ascii=False)
        print("Successfully saved QCF text to", out_path)
except Exception as e:
    print("Failed to download JSON:", e)

# 2. Download 604 QCF Fonts
print("Downloading 604 fonts...")
font_base_url = "https://raw.githubusercontent.com/quran/quran.com-images/master/res/fonts/QCF_P{page:03d}.TTF"

def download_font(page):
    url = font_base_url.format(page=page)
    filename = f"QCF_P{page:03d}.TTF"
    filepath = os.path.join(fonts_dir, filename)
    
    if os.path.exists(filepath) and os.path.getsize(filepath) > 10000:
        return f"{filename} already exists."
        
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
    try:
        with urllib.request.urlopen(req, timeout=10) as resp:
            data = resp.read()
            with open(filepath, "wb") as f:
                f.write(data)
            return f"Downloaded {filename}"
    except Exception as e:
        return f"Failed {filename}: {e}"

with concurrent.futures.ThreadPoolExecutor(max_workers=5) as executor:
    futures = {executor.submit(download_font, p): p for p in range(1, 605)}
    for count, future in enumerate(concurrent.futures.as_completed(futures), 1):
        if count % 20 == 0 or count > 550:
            try:
                print(f"Downloaded {count}/604 fonts... Last result: {future.result()}")
            except Exception as exc:
                print(f"Error on {futures[future]}: {exc}")

print("All downloads complete!")

# 3. Download header fonts (if necessary for surah names and Bismillah)
# Bismillah QCF is usually part of QCF_BSML.TTF, and Surah names in QCF_BSML or similar.
# Wait, QCF V1 page fonts ALL have the Bismillah glyph usually, or we can just use the Bismillah unicode text in the v1 mapping.
# The API data contains the bismillah code for each surah if we need it.
