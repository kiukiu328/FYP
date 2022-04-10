import StreamReceiver
import threading
from face_detect2 import FaceDetect
from flask import Flask, send_file, Response, request
import json, re
from datetime import datetime
import socket
import cv2
import base64
from threading import Timer
import AlertMessaging
from FirebaseDB import UpdateAddress, InitFirebase
from MySQLDatabase import UploadVideoRecord, GetAlertRecord,UpdateAlertRecord
import GetNetworkAddress
import os, sys
from pathlib import Path
import subprocess
import Detect_Police

# Server Communication
server = None
face_detect_module = FaceDetect

# 图片数据传输
HOST = ''
ADDRESS = ""

# 创建一个套接字
# tcpServer = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
tcpServer = ""
# 绑定本地ip
# tcpServer.bind(ADDRESS)


stream_img = ""
# 每10秒刷一次
timing_picture = ""

# alert funciton的参数
alert_signal = True
num = 0

# 检测后的 图片
detectedImage = ""
policeImage = ""
showDetectedImage = True

# 写入视屏
flag_write_video = True
# 是否正在录像
# true:在录像 false:没在录像
recording_state = False

# 計算識別指定object 幾秒發送alert
alert_timer = 10

# 圖片更換的時間，預設 5秒
changePictureTime = 5


# 把视频写成文件
def write_video(out):
    global flag_write_video, server
    while 1:
        out.write(server.frame2)
        cv2.waitKey(5)
        if flag_write_video == False:
            break


def video_write_init(video_name):
    global flag_write_video, server
    flag_write_video = True

    fps = 20.0  # 指定写入帧率为20

    codec = cv2.VideoWriter_fourcc('H', '2', '6', '4')
    # day-hour-min-second

    video_Buffer = sys.path[0] + f"\\BufferVideo\\{video_name}.avi"
    target_path = sys.path[0] + f'\\UserSourceFile\\video\\{video_name}.mp4'

    # print(video_Buffer)

    out = cv2.VideoWriter(video_Buffer, codec, fps, (server.frame2.shape[1], server.frame2.shape[0]))
    # 写入frame
    write_video(out)
    print("stop recording")
    # UploadVideoRecord.save_video(video_path)

    zipVideo(video_Buffer, target_path)
    out.release()


# 轉換成 可播放格式
def zipVideo(file, target):
    # dir = file.strip(".avi")
    # command = "ffmpeg -i %s.avi %s.mp4" % (dir, target)
    # call(command.split())
    return_msg = subprocess.call('ffmpeg -i ' + file.replace(" ", "\\ ") + " -c:v libx264 -preset medium -qp 35 " + target.replace(" ", "\\ "),shell=True)
    if return_msg==0:
        UpdateAlertRecord.updateVideoState(file_name, 1)


# 截图
def videoPhotoCapture(icon_name):
    global server
    # icon_name = f"{date_obj.month}_{date_obj.day}_{date_obj.hour}_{date_obj.minute}_{date_obj.second}"
    icon_path = f'src/UserSourceFile\\icon\\{icon_name}.jpg'
    cv2.imwrite(icon_path, server.frame2)


# 因为 我选择的是延迟5秒关闭视屏
def close_recording():
    global flag_write_video
    # 停止录像
    flag_write_video = False


# 时间为基准的 file 名字
def generateFileName():
    date_obj = datetime.now()
    video_name = f"{date_obj.month}_{date_obj.day}_{date_obj.hour}_{date_obj.minute}_{date_obj.second}"
    return video_name


def alert():
    global num, face_detect_module, alert_signal, flag_write_video, recording_state, alert_timer
    count = face_detect_module.count
    # print("Detected time is:", num, "Count is:", count)

    if count == 0:
        num = 0
        alert_signal = True

        if (recording_state == True):
            # 停止录像
            Timer(1, close_recording).start()
            recording_state = False

    elif count > 0:
        num += 1

    if (num == alert_timer and alert_signal == True):
        alert_signal = False

        print("Start recording")
        # 开启录像 和 截图
        recording_state = True
        # 生成文件名
        fileName_new = generateFileName()

        iconRecord = threading.Thread(target=videoPhotoCapture, args=(fileName_new,))
        iconRecord.start()
        videoRecord = threading.Thread(target=video_write_init, args=(fileName_new,))
        videoRecord.start()

        UploadVideoRecord.save_video(fileName_new)
        # 把 记录 储存到数据库

        print("Alert now")
        AlertMessaging.messaging_send("Security reminder", "Someone is looking at your car for a long time")

    timer = threading.Timer(1, alert)
    timer.start()


# 图片数据传输
def convert_frame(frame):
    if (len(frame) != 0):
        img_encode = cv2.imencode('.jpg', frame, [cv2.IMWRITE_JPEG_QUALITY, 80])[1]
        frame = base64.b64encode(img_encode)
        # frame = base64.b64encode(frame)
        flag_data = str(len(frame)).encode() + ",".encode()
        return flag_data + frame
    else:
        return "".encode()


def pictuer_timing():
    # global timing_picture, server, changePictureTime

    if (server is not None and len(server.frame2) != 0):
        timing_picture = server.frame2
        # print(len(timing_picture))
    Timer(changePictureTime, pictuer_timing).start()


# 视屏直播
def start_stream_server():
    global stream_img, tcpServer, server, showDetectedImage, detectedImage, timing_picture, policeImage
    # 开始监听
    tcpServer.listen(5)
    print(ADDRESS)

    while 1:
        client_socket, client_address = tcpServer.accept()
        # print("连接成功！", client_address)
        # tcpServer.send(b"success")
        if client_socket:
            try:
                while 1:

                    # 读取图像
                    # 通过 flag 控制 图像 返回什么样的类型
                    # True:返回显示框框的图像
                    # False: 返回原图片
                    if showDetectedImage:
                        cv_image = policeImage
                    else:
                        cv_image = server.frame2
                    # # 压缩图像
                    # img_encode = cv2.imencode('.jpg', cv_image, [cv2.IMWRITE_JPEG_QUALITY, 80])[1]

                    response = client_socket.recv(1024)
                    # print(response.decode())
                    # print(len(img_encode))
                    if response.decode() == 'stream':
                        stream_img = convert_frame(cv_image)
                        client_socket.send(stream_img)
                        # client_socket.close()
                        break
                    elif response.decode() == 'picture':
                        client_socket.send(convert_frame(timing_picture))
                        break
            finally:
                client_socket.close()
                # print("test")


# 图篇数据传输 end


# 人脸检测
def print_frame():
    global server, face_detect_module, detectedImage, policeImage
    alert()
    while 1:
        if (server is not None and len(server.frame2) != 0):
            if (len(server.frame2) != 0):
                # print frame
                # print(server.frame2)
                detectedImage = face_detect_module.detect(server.frame2)
                policeImage = Detect_Police.detect(server.frame2)
                # true and false

                cv2.imshow("policeImage", policeImage)
                # cv2.waitKey(1)


# 启动服务器
def start_server():
    global server

    # 启动检测
    td1 = threading.Thread(target=print_frame)
    td1.start()
    td3 = threading.Thread(target=pictuer_timing)
    td3.start()
    td4 = threading.Thread(target=start_stream_server)
    td4.start()
    td5 = threading.Thread(target=clearBufferVideo)
    td5.start()

    # 最后开启服务器
    if server is None:
        server = StreamReceiver
        server.start()


# 刪除所有原 視頻文件
# 不能亂動
def clearBufferVideo():
    root_dir = sys.path[0] + "\\BufferVideo"
    for subdirs, dirs, files in os.walk(root_dir):
        for file in files:
            os.remove(subdirs + "\\" + file)
            # print(subdirs+"\\"+file)
    print("remove buffer video file")

    # 一小時自己清除
    clear = Timer(60 * 60, clearBufferVideo)
    clear.start()


# 初始化 WLAN address
def init_WLAN_address():
    global tcpServer, ADDRESS, HOST
    InitFirebase.init()
    HOST = GetNetworkAddress.get_WLAN_address()

    # save the host data to firebase
    UpdateAddress.save_network_info(HOST)

    PORT = 9990

    ADDRESS = (HOST, PORT)
    tcpServer = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    print(ADDRESS)
    tcpServer.bind(ADDRESS)


app = Flask(__name__)


@app.route('/')  # 主页
def index():
    global alert_signal

    time = datetime.now()
    string_time = time.strftime('%Y-%m-%d %H:%M:%S')
    data = {"detect_result": alert_signal, "time": string_time}
    json_string = json.dumps(data)
    return json_string


@app.route("/image/<imageName>")
def getImage(imageName):
    image = r"UserSourceFile\icon\{}.jpg".format(imageName)
    # resp = Response(image,mimetype="image/jpeg")
    return send_file(image, mimetype="image/jpeg")


CHUNK_SIZE = 10 ** 25
BASE_DIR = Path('.')


@app.after_request
def after_request(response):
    response.headers.add('Accept-Ranges', 'bytes')
    return response


def get_chunk(full_path, byte1=None, byte2=None):
    if not os.path.isfile(full_path):
        raise OSError('no such file: {}'.format(full_path))

    file_size = os.stat(full_path).st_size
    start = 0
    if byte1 < file_size:
        start = byte1

    length = file_size - start
    if byte2:
        length = byte2 + 1 - byte1

    if length > CHUNK_SIZE:
        length = CHUNK_SIZE

    with open(full_path, 'rb') as f:
        f.seek(start)
        chunk = f.read(length)
    return chunk, start, length, file_size


def get_byte_interval(request):
    range_header = request.headers.get('Range', None)
    byte1, byte2 = 0, None
    if range_header:
        match = re.search(r'(\d+)-(\d*)', range_header)
        groups = match.groups()

        if groups[0]:
            byte1 = int(groups[0])
        if groups[1]:
            byte2 = int(groups[1])

    return byte1, byte2


@app.route("/video/<videoName>")
def getVideo(videoName):
    video = r"UserSourceFile/video/{}.mp4".format(videoName)
    byte1, byte2 = get_byte_interval(request)

    chunk, start, length, file_size = get_chunk(
        BASE_DIR / video,
        byte1, byte2)

    resp = Response(chunk, 206, mimetype='video/mp4',
                    content_type='video/mp4', direct_passthrough=True)

    resp.headers.add('Accept-Ranges', 'bytes')
    resp.headers.add('Content-Range', 'bytes {0}-{1}/{2}'.format(start, start + length - 1, file_size))

    return resp


@app.route("/AlertRecordJSON")
def getAlertJSON():
    return GetAlertRecord.getDataJson()


@app.route("/setParameters", methods=["GET", "POST"])
def setD():
    global alert_timer, changePictureTime
    detectTime = request.args.get("detectTime")

    prePictureTime = request.args.get("previewPictureTime")
    if detectTime != None:
        alert_timer = int(detectTime)

    if prePictureTime != None:
        changePictureTime = int(prePictureTime)

    return "successful change"


@app.route("/getParameters")
def getD():
    global alert_timer, changePictureTime
    json_result = {"DetectTime": str(alert_timer), "PreviewPictureTime": str(changePictureTime)}
    result = json.dumps(json_result)
    return result


if __name__ == '__main__':
    init_WLAN_address()
    td2 = threading.Thread(target=start_server)
    td2.start()
    app.run(host=HOST, port=8080)
