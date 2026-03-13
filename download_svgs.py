import urllib.request
import os
import concurrent.futures
import json

os.makedirs('app/src/main/assets/surah_names', exist_ok=True)

try:
    x = json.loads(urllib.request.urlopen('https://api.github.com/repos/gyenabubakar/surah-name-glyphs/contents/svg').read().decode())
    urls = {a['name']: a['download_url'] for a in x}
except Exception as e:
    print("Failed to get file list", e)
    urls = {}

def download_surah(item):
    filename, url = item
    out_path = f"app/src/main/assets/surah_names/{filename}"
    
    # rename "0. surah.svg" to "0.svg" for easier loading
    if filename == "0. surah.svg":
        out_path = "app/src/main/assets/surah_names/0.svg"
        
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req) as response:
            with open(out_path, 'wb') as f:
                f.write(response.read())
        return f"Downloaded {filename}"
    except Exception as e:
        return f"Failed {filename}: {e}"

if urls:
    with concurrent.futures.ThreadPoolExecutor(max_workers=10) as executor:
        results = list(executor.map(download_surah, urls.items()))

    print("Finished downloading SVGs. Successes:", sum(1 for r in results if "Downloaded" in r))
else:
    print("No URLs to download")
