import urllib.request
import json
import concurrent.futures
import time
import os
import sys

BASE_URL = "https://hadeethenc.com/api/v1"
LANG = "ar"
OUTPUT_FILE = "app/src/main/assets/hadiths.json"

def fetch_json(url):
    for i in range(3):
        try:
            req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
            with urllib.request.urlopen(req, timeout=10) as response:
                return json.loads(response.read().decode('utf-8'))
        except Exception as e:
            time.sleep(1)
    return None

def get_categories():
    url = f"{BASE_URL}/categories/list/?language={LANG}"
    res = fetch_json(url)
    return res if res else []

def get_hadiths_for_category(category_id, target_count=20):
    hadiths = []
    page = 1
    while len(hadiths) < target_count:
        url = f"{BASE_URL}/hadeeths/list/?language={LANG}&category_id={category_id}&page={page}&per_page=50"
        res = fetch_json(url)
        if not res or 'data' not in res or len(res['data']) == 0:
            break
            
        fetched = res['data']
        needed = target_count - len(hadiths)
        hadiths.extend(fetched[:needed])
        
        meta = res.get('meta', {})
        current_page = int(meta.get('current_page', page))
        last_page = int(meta.get('last_page', page))
        if current_page >= last_page:
            break
        page += 1
        
    return hadiths

def get_hadith_details(hadith_id):
    url = f"{BASE_URL}/hadeeths/one/?language={LANG}&id={hadith_id}"
    return fetch_json(url)

print(">>> Starting HadeethEnc Mass Downloader <<<")
print("1. Fetching all available categories...")
categories_data = get_categories()

# Filter for the most important categories requested by the user
# We'll put the "General Important Hadiths" at the very beginning
USER_CATEGORIES = [
    ("أهم الأحاديث (جوامع الدّين)", ["جوامع", "نووي", "أربعين", "متواتر", "فضل", "صحيح"]),
    ("الإيمان", ["الإيمان", "العقيدة", "التوحيد"]),
    ("الصلاة", ["الصلاة"]),
    ("الأخلاق", ["الأخلاق", "الشمائل"]),
    ("الذكر والدعاء", ["الذكر", "الدعاء", "الأذكار"]),
    ("النية والإخلاص", ["النية", "الإخلاص"]),

    ("بر الوالدين", ["بر الوالدين", "الوالدين"]),
    ("صلة الرحم", ["صلة الرحم", "الأرحام", "الرحم"]),
    ("حسن المعاملة", ["حسن الخلق", "المعاملة", "التعامل", "المعاملات"]),
    ("الصبر", ["الصبر", "الابتلاء"]),
    ("التوبة", ["التوبة", "الاستغفار"]),
    ("العلم", ["العلم", "العلماء"]),
    ("الأسرة", ["الأسرة", "النكاح", "البيوت", "الزوج"]),
    ("العمل الحلال", ["الكسب", "البيوع", "العمل"]),
    ("الصدقة", ["الصدقة", "الزكاة"]),
    ("الصيام", ["الصيام", "الصوم"]),
    ("الحج", ["الحج", "العمرة"]),
    ("الجنة", ["الجنة"]),
    ("النار", ["النار"]),
    ("الفتن", ["الفتن", "أشراط الساعة", "الملاحم"]),
    ("آداب المسلم", ["الآداب", "آداب"])
]

filtered_cats = []
used_category_ids = set()

for target_name, keywords in USER_CATEGORIES:
    matched = []
    for c in categories_data:
        if c['id'] in used_category_ids: continue
        if any(kw in c['title'] for kw in keywords):
            matched.append(c)
            used_category_ids.add(c['id'])
            # If it's not the first general category, 1 match is enough
            if target_name != "أهم الأحاديث (جوامع الدّين)":
                break
            # For the general category, try to get up to 3 sub-categories to pull 50 hadiths from
            if len(matched) >= 3:
                break
                
    if matched:
        filtered_cats.append({
            "target_name": target_name,
            "matched_ids": [int(m['id']) for m in matched],
        })

categories_data = filtered_cats
print(f"-> Mapped {len(categories_data)} requested categories based on exact requirements.\n")

categories_list = []
for idx, c in enumerate(categories_data):
    categories_list.append({
        "id": idx + 1, # Re-indexing cleanly for the app
        "title": c['target_name']
    })

print("2. Fetching hadith lists for each category...")
all_hadith_refs = []
for idx, c in enumerate(categories_data):
    # App-side category ID
    app_cat_id = idx + 1 
    
    # If it's our first "General" category, fetch 50 hadiths, else fetch 20
    is_general_cat = idx == 0
    limit = 50 if is_general_cat else 20
    
    h_list = []
    # Pull from all matched HadeethEnc categories for this target theme
    for src_cat_id in c['matched_ids']:
        rem = limit - len(h_list)
        if rem <= 0: break
        sub_list = get_hadiths_for_category(src_cat_id, target_count=limit)
        h_list.extend(sub_list[:rem])
        
    for h in h_list:
        all_hadith_refs.append({
            'id': int(h['id']),
            'category_id': app_cat_id
        })
    print(f"[{idx+1}/{len(categories_data)}] Category {app_cat_id} ({c['target_name']}): found {len(h_list)} hadiths.")

# Deduplicate
unique_hadiths = {}
for ref in all_hadith_refs:
    if ref['id'] not in unique_hadiths:
        unique_hadiths[ref['id']] = ref['category_id']

total_hadiths = len(unique_hadiths)
print(f"\n-> Total unique hadiths to fetch: {total_hadiths}\n")

def process_hadith(h_id_cat):
    h_id, cat_id = h_id_cat
    details = get_hadith_details(h_id)
    if not details:
        return None
        
    vocab = []
    if 'words_meanings' in details and details['words_meanings']:
        for wm in details['words_meanings']:
            w_str = wm.get('word') or ''
            m_str = wm.get('meaning') or ''
            vocab.append({
                "word": str(w_str).strip(), 
                "meaning": str(m_str).strip()
            })
            
    apps = []
    if 'hints' in details and details['hints']:
        apps = [str(h).strip() for h in details['hints'] if h is not None]
        
    attr_str = details.get('attribution') or 'غير محدد'
    hadeeth_str = details.get('hadeeth') or details.get('title') or ''
    ref_str = details.get('reference') or ''
    exp_str = details.get('explanation') or ''
    
    return {
        "id": int(details['id']),
        "bookId": 1,
        "categoryId": cat_id,
        "narrator": str(attr_str).strip(),
        "text": str(hadeeth_str).strip(),
        "source": str(ref_str).strip(),
        "explanation": str(exp_str).strip(),
        "vocabulary": vocab,
        "lifeApplications": apps
    }

print("3. Fetching detailed data for each hadith (Explanations, Vocabulary, Apps)...")
start_time = time.time()
success_results = []
count = 0

# Using 15 max workers to avoid being instantly rate-limited while still being extremely fast.
with concurrent.futures.ThreadPoolExecutor(max_workers=15) as executor:
    # Submit all tasks
    futures = {executor.submit(process_hadith, item): item for item in unique_hadiths.items()}
    
    for future in concurrent.futures.as_completed(futures):
        res = future.result()
        if res:
            success_results.append(res)
        count += 1
        if count % 100 == 0 or count == total_hadiths:
            sys.stdout.write(f"\rProgress: {count}/{total_hadiths} hadiths downloaded... ({(count/total_hadiths)*100:.1f}%)")
            sys.stdout.flush()

print(f"\n\n-> Finished downloading {len(success_results)} hadiths in {time.time() - start_time:.2f} seconds!")

final_json = {
    "books": [
        {
            "id": 1,
            "name": "موسوعة الأحاديث النبوية (HadeethEnc)"
        }
    ],
    "categories": categories_list,
    "hadiths": success_results
}

os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)
with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
    json.dump(final_json, f, ensure_ascii=False, indent=2)

print(f"\n[SUCCESS] Saved everything straight into app data: {OUTPUT_FILE}")
print("You can now rebuild the app and enjoy the full dataset!")
