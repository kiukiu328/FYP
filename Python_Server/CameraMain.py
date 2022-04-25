import time

import imagiz
import cv2
import GetNetworkAddress

vid = cv2.VideoCapture("src/3.mp4")
# vid = cv2.VideoCapture(1)
client = imagiz.TCP_Client(server_ip=GetNetworkAddress.get_WLAN_address(), server_port=8880, client_name="cc1")
encode_param = [int(cv2.IMWRITE_JPEG_QUALITY), 90]

while True:
    # time.sleep(0.05)
    r, frame = vid.read()
    if r:
        r, image = cv2.imencode('.jpg', frame, encode_param)
        response = client.send(image)
    if not r:
        vid = cv2.VideoCapture("src/3.mp4")