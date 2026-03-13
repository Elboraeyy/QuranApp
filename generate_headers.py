import json

# Load verses to find where surahs start (page and first word)
quran = json.load(open('app/src/main/assets/qcf_quran.json', encoding='utf-8'))['verses']
lines_data = json.load(open('app/src/main/assets/qcf_lines.json', encoding='utf-8'))
surahs = json.load(open('app/src/main/assets/surahs.json', encoding='utf-8'))

# We want a list of Surah Headers to inject per page.
# A page can have 0, 1, or 2 surah headers.
page_headers = {} # page -> [ {'line': L, 'type': 'surah', 'surah': 5}, ... ]

surah_starts = {}
for verse in quran:
    surah, ayah = map(int, verse['verse_key'].split(':'))
    if ayah == 1:
        surah_starts[surah] = verse

for surah in sorted(surah_starts.keys()):
    v1 = surah_starts[surah]
    page = str(v1['v1_page'])
    if page not in lines_data:
        continue
    
    # What line does this surah start on?
    # We can search the code_v1 of Verse 1 in the page's lines.
    v1_first_word = v1['code_v1'].split()[0] if v1['code_v1'] else ''
    
    first_line_of_surah = 15
    for ln in sorted(map(int, lines_data[page].keys())):
        if v1_first_word in lines_data[page][str(ln)]:
            first_line_of_surah = ln
            break
            
    # Now we know where the Surah starts.
    # The header is above it.
    header_line = first_line_of_surah - 2
    bismillah_line = first_line_of_surah - 1
    
    if surah == 9: # At-Tawba has no Bismillah
        header_line = first_line_of_surah - 1
        bismillah_line = -1
        
    if surah == 1:
        # Fatiha verse 1 IS the bismillah. Header is above it.
        header_line = first_line_of_surah - 1
        bismillah_line = -1
        
    if page not in page_headers:
        page_headers[page] = []
        
    if header_line > 0:
        page_headers[page].append({ 'lineNumber': header_line, 'surahNumber': surah, 'isBismillah': False })
    if bismillah_line > 0:
        page_headers[page].append({ 'lineNumber': bismillah_line, 'surahNumber': surah, 'isBismillah': True })

with open('app/src/main/assets/surah_headers.json', 'w') as f:
    json.dump(page_headers, f, indent=2)

print("Saved surah_headers.json with", sum(len(x) for x in page_headers.values()), "headers across", len(page_headers), "pages.")
