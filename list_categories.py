import json
with open('app/src/main/assets/adhkar.json', encoding='utf-8') as f:
    data = json.load(f)
for c in data['categories']:
    print(f"{c['id']}: {c['title']}")
