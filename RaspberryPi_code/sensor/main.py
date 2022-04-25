from flask import Flask, jsonify, request, send_file
import json
import bme280
import cv2


app = Flask(__name__)
voltage = None
# vid = cv2.VideoCapture(0)
@app.route('/')
def data():
    temperature,pressure,humidity = bme280.readBME280All()
    d = {'temperature': temperature,
         'pressure': pressure,
         'humidity':humidity,
         'voltage':voltage
         }
    return json.dumps(d)


@app.route('/setVoltage', methods=['GET'])
def setVoltage():
    global voltage
    voltage = float(request.args['voltage'])
    return 'voltage set'


# @app.route('/camera')
# def camera():
    # global vid
    # r,image = vid.read()
    # cv2.imwrite('camera.jpg',image)
    # return send_file('camera.jpg')
    

if __name__ == '__main__':
    app.run(host='0.0.0.0',port=80)
