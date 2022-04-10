#!C:\Users\kiu\AppData\Local\Programs\Python\Python39\python.exe
import json
import os
import urllib.parse
import requests as requests

datatype = str(urllib.parse.parse_qs(os.environ['QUERY_STRING'])['type'])[2:-2]
print('Content-Type: application/json; charset=utf-8\n')
if(datatype == 'vacancy'):
    url = "https://api.data.gov.hk/v1/carpark-info-vacancy"
    info = requests.get(url).json()['results']
    url = "https://api.data.gov.hk/v1/carpark-info-vacancy?data=vacancy"
    vacancy = requests.get(url).json()['results']

    for x in range(len(info)):
        vacancy[x].update(info[x])
    with open('vacancy.json', 'w') as f:
        json.dump(vacancy, f)
    print(json.dumps(vacancy))
elif(datatype == 'echarge_en'):
    print(open("echarge_en.json", "r").read())
elif(datatype == 'echarge_zh'):
    print(open("echarge_zh.json", "r").read())