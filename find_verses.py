import json
import re

with open('app/src/main/assets/adhkar.json', encoding='utf-8') as f:
    data = json.load(f)

for cat in data['categories']:
    for item in cat['items']:
        text = item['text']
        if 'أَعُوذُ' in text or 'بِسْم' in text or '\n' in text:
            if 'الشَّيْطَانِ' in text or 'الرَّحِيم' in text:
                print(f"ID: {item['id']}")
                print(f"Text:\n{text}")
                print(f"Ref:\n{item['reference']}")
                print("-" * 40)
