
import requests
import json

def check_bismillah():
    # Surah 2 (Al-Baqara) Ayah 1
    url = "http://api.alquran.cloud/v1/ayah/2:1/quran-uthmani"
    response = requests.get(url)
    if response.status_code == 200:
        data = response.json()
        text = data['data']['text']
        print(f"Full text: {text}")
        print("Character hex codes:")
        for char in text:
            print(f"{char}: {hex(ord(char))}")
    else:
        print(f"Error: {response.status_code}")

if __name__ == "__main__":
    check_bismillah()
