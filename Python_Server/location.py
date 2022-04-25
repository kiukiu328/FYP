import cv2
from flask import Flask, request, send_file
import mysql.connector
import geopy.distance
import AlertPolice
import GetNetworkAddress

mydb = mysql.connector.connect(
    host="localhost",
    user="root",
    password="",
    database="fyp_project"
)

cursor = mydb.cursor()

app = Flask(__name__)


@app.route('/location', methods=['POST'])
def location():
    data = request.form
    id = data['id']
    latitude = data['latitude']
    longitude = data['longitude']
    try:
        sql = f'INSERT INTO location VALUES ("{id}", {latitude}, {longitude})'
        cursor.execute(sql)
        mydb.commit()
        print(cursor.rowcount, "record inserted.")
    except:
        sql = f'UPDATE location SET latitude={latitude}, longitude={longitude} WHERE id="{id}"'
        cursor.execute(sql)
        mydb.commit()
        print(cursor.rowcount, "record UPDATE.")

    return 'yes'


countingFiveMin = False
timeMarker = None
from datetime import datetime, timedelta


@app.route('/locationAlert', methods=['POST'])
def locationAlert():
    global countingFiveMin, timeMarker

    if not countingFiveMin:
        timeMarker = datetime.now()
        countingFiveMin = True
        count = 0
        data = request.form
        id = data['id']
        sql = f'SELECT * FROM location where id="{id}"'
        cursor.execute(sql)
        records = cursor.fetchall()
        if len(records) > 0:
            original = (records[0][1], records[0][2])
            print(f'original: {original}')
            sql = f'SELECT * FROM location where not id="{id}"'
            cursor.execute(sql)
            records = cursor.fetchall()
            for r in records:
                print(f'r: {r}')
                cur = (r[1], r[2])
                distance = geopy.distance.geodesic(original, cur).m
                print(f'id:{r[0]} || distance: {distance}(m)')
                if distance < 300:
                    print(f'Alert {r[0]}')
                    print(f'r[3] {r[3]}')
                    print(f'[r[3]] {[r[3]]}')
                    AlertPolice.sendPush('Parking Alert', f'Detects police {distance:.0f} meters away', [r[3]])
                    count += 1
        return f'Alerted {count} device(s)'
    if (datetime.now() - timeMarker) > timedelta(minutes=5):
        countingFiveMin = False
    return f'Timer not yet'


@app.route('/sendToken', methods=['POST'])
def sendToken():
    data = request.form
    id = data['id']
    token = data['token']
    sql = f'UPDATE location SET token="{token}" WHERE id="{id}"'
    cursor.execute(sql)
    mydb.commit()
    print(cursor.rowcount, "record UPDATE.")
    return f'id: {id}  || token: {token}'

@app.route('/camera')
def camera():
    global vid
    r, image = vid.read()
    cv2.imwrite('./src/camera.jpg', image)
    return send_file('./src/camera.jpg')

if __name__ == '__main__':
    app.run(host=GetNetworkAddress.get_WLAN_address())
