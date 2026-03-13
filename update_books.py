import json

FILE_PATH = "app/src/main/assets/hadiths.json"

BOOKS = [
    {"id": 1, "title": "متفق عليه", "author": "البخاري ومسلم", "description": "أعلى درجات الصحة"},
    {"id": 2, "title": "صحيح البخاري", "author": "الإمام البخاري", "description": "الجامع المسند الصحيح"},
    {"id": 3, "title": "صحيح مسلم", "author": "الإمام مسلم", "description": "المسند الصحيح"},
    {"id": 4, "title": "سنن الترمذي", "author": "الإمام الترمذي", "description": "الجامع المختصر"},
    {"id": 5, "title": "سنن أبي داود", "author": "الإمام أبو داود", "description": "جمع سنن الأحكام"},
    {"id": 6, "title": "سنن النسائي", "author": "الإمام النسائي", "description": "السنن الصغرى (المجتبى)"},
    {"id": 7, "title": "سنن ابن ماجه", "author": "الإمام ابن ماجه", "description": "خاتم الكتب الستة"},
    {"id": 8, "title": "موطأ مالك", "author": "الإمام مالك بن أنس", "description": "أقدم كتب الحديث المعتمدة"},
    {"id": 9, "title": "مسند أحمد", "author": "الإمام أحمد بن حنبل", "description": "أكبر دواوين السنة"},
    {"id": 10, "title": "كتب أخرى", "author": "أئمة الحديث", "description": "مصادر أخرى معتمدة"}
]

def map_book_id(source):
    s = source.lower() if source else ""
    if "متفق عليه" in s: return 1
    if "البخاري" in s: return 2
    if "مسلم" in s: return 3
    if "الترمذي" in s: return 4
    if "أبو داود" in s or "أبي داود" in s: return 5
    if "النسائي" in s: return 6
    if "ابن ماجه" in s or "ابن ماجة" in s: return 7
    if "موطأ" in s or "مالك" in s: return 8
    if "أحمد" in s: return 9
    return 10

def run():
    with open(FILE_PATH, 'r', encoding='utf-8') as f:
        data = json.load(f)
        
    data['books'] = BOOKS
    
    count_changes = 0
    for h in data.get('hadiths', []):
        new_id = map_book_id(h.get('source', ''))
        if h.get('bookId') != new_id:
            h['bookId'] = new_id
            count_changes += 1

    with open(FILE_PATH, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
        
    print(f"Assigned actual Hadith Books to {len(data['hadiths'])} hadiths! Modified {count_changes} records based on their source Strings.")

if __name__ == "__main__":
    run()
