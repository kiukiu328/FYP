import imagiz
import cv2
import GetNetworkAddress

frame1=""
frame2 = ""

def start():
    global frame1,frame2
    server=imagiz.TCP_Server(8880,ip=GetNetworkAddress.get_WLAN_address())
    server.start()
    while True:
        message=server.receive()
        frame=cv2.imdecode(message.image,1)
        ret, jpeg = cv2.imencode('.jpg', frame)
        frame1 = jpeg
        frame2 = frame


