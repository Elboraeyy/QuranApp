import json

quran = json.load(open('app/src/main/assets/qcf_quran.json', encoding='utf-8'))['verses']
lines_data = json.load(open('app/src/main/assets/qcf_lines.json', encoding='utf-8'))

surah_starts = {}
for verse in quran:
    surah, ayah = map(int, verse['verse_key'].split(':'))
    if ayah == 1:
        surah_starts[surah] = verse

for surah, verse in surah_starts.items():
    page = str(verse['v1_page'])
    if page not in lines_data:
        continue
    
    page_lines = set(map(int, lines_data[page].keys()))
    
    # We need to find the line number of verse 1.
    # We can get it from the API? Actually `v1_page` doesn't have the line number here.
    # Let's just find the max line number BEFORE the verse. Or we know page_lines.
    
print("Done")
