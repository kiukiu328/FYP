import cv2
import threading
import pandas
import imutils
from datetime import datetime
count = 0
# num=0
# detect_result = False

# Assigning our static_back to None
static_back = None
# List when any moving object appear
motion_list = [None, None]
# Time of movement
time = []
# Initializing DataFrame, one column is start
# time and other column is end time
df = pandas.DataFrame(columns=["Start", "End"])
def motion_detect(frame):
    global static_back,motion_list,time,df
    motion = 0

    # Converting color image to gray_scale image
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    # Converting gray scale image to GaussianBlur
    # so that change can be find easily
    gray = cv2.GaussianBlur(gray, (21, 21), 0)

    # In first iteration we assign the value
    # of static_back to our first frame
    if static_back is None:
        static_back = gray

    # Difference between static background
    # and current frame(which is GaussianBlur)
    diff_frame = cv2.absdiff(static_back, gray)

    # If change in between static background and
    # current frame is greater than 30 it will show white color(255)
    thresh_frame = cv2.threshold(diff_frame, 30, 255, cv2.THRESH_BINARY)[1]
    thresh_frame = cv2.dilate(thresh_frame, None, iterations=2)

    # Finding contour of moving object
    cnts, _ = cv2.findContours(thresh_frame.copy(),
                               cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    for contour in cnts:
        if cv2.contourArea(contour) < 10000:
            continue
        motion = 1

        (x, y, w, h) = cv2.boundingRect(contour)
        # making green rectangle around the moving object
        cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 3)

    # Appending status of motion
    motion_list.append(motion)

    motion_list = motion_list[-2:]

    # Appending Start time of motion
    if motion_list[-1] == 1 and motion_list[-2] == 0:
        time.append(datetime.now())

    # Appending End time of motion
    if motion_list[-1] == 0 and motion_list[-2] == 1:
        time.append(datetime.now())

    return frame;



# model_bin = "res10_300x300_ssd_iter_140000_fp16.caffemodel";
# config_text = "deploy.prototxt";
model_bin = "../src/res10_300x300_ssd_iter_140000_fp16.caffemodel"
config_text = "../src/deploy.prototxt"

# load caffe model
net = cv2.dnn.readNetFromCaffe(config_text, model_bin)

# set back-end
net.setPreferableBackend(cv2.dnn.DNN_BACKEND_OPENCV)
net.setPreferableTarget(cv2.dnn.DNN_TARGET_CPU)
def face_detect(image):
    global count
    # timer = threading.Timer(1, alert)
    # timer.start()
    # cap = cv2.VideoCapture("5.mp4")
    # cap = cv2.VideoCapture(0)
    # cap = cv2.VideoCapture("5.mp4")
    # cap = cv2.VideoCapture("4.mp4")
        # image = cv2.flip(image, 1)
        # 人脸检测
    h, w = image.shape[:2]
    blobImage = cv2.dnn.blobFromImage(image, 1.0, (300, 300), (104.0, 177.0, 123.0), False, False);
    net.setInput(blobImage)
    cvOut = net.forward()

    # Put efficiency information.
    t, _ = net.getPerfProfile()
    count=0
    fps = 1000 / (t * 1000.0 / cv2.getTickFrequency())
    label = 'FPS: %.2f' % fps
    cv2.putText(image, label, (5, 15), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0))
    # 绘制检测矩形
    for detection in cvOut[0,0,:,:]:
        score = float(detection[2])
        # objIndex = int(detection[1])
        if score > 0.5:

            left = detection[3]*w
            top = detection[4]*h
            right = detection[5]*w
            bottom = detection[6]*h


            if((bottom-top) > 100):
                count += 1

                text_size_ = cv2.getTextSize("Danger",cv2.FONT_HERSHEY_SIMPLEX,0.5,thickness=1)
                text_width,text_height = text_size_[0]

                # 绘制
                cv2.rectangle(image, (int(left), int(top)), (int(right), int(bottom)), (0, 0, 255), thickness=2)

                cv2.rectangle(image, (int(left) - 15, int(top) - 8),(int(left) - 15 + text_width, int(top) - 10-text_height), (0, 0, 255), -1)
                cv2.putText(image, "Vigilant", (int(left)-15, int(top)-10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 1)
                cv2.putText(image, "score:%.2f"%score, (int(left)+50, int(top)-10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0,255 ), 1)
            else:
                text_size_ = cv2.getTextSize("Normal", cv2.FONT_HERSHEY_SIMPLEX, 0.5, thickness=1)
                text_width, text_height = text_size_[0]

                cv2.rectangle(image, (int(left), int(top)), (int(right), int(bottom)), (0, 255, 0), thickness=2)
                cv2.rectangle(image, (int(left) - 15, int(top) - 8),(int(left) - 15 + text_width, int(top) - 10-text_height), (0, 255, 0), -1)
                cv2.putText(image, "Normal", (int(left)-15, int(top)-10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 1)
                cv2.putText(image, "score:%.2f" % score, (int(left)+50, int(top)-10), cv2.FONT_HERSHEY_SIMPLEX, 0.5,(0, 255, 0), 1)

    cv2.putText(image, "Count: %s"%count, (int(5), int(35)), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0),1)
    return image



# def detect():
#     # cap = cv2.VideoCapture(0)
#     cap = cv2.VideoCapture("5.mp4")
#     while True:
#         ret, image = cap.read()
#         # image = cv2.flip(image, 1)
#         if ret is False:
#             break
#
#         # image= motion_detect(image)
#         image = face_detect(image)
#         image =motion_detect(image)
#         cv2.imshow('face-detection-demo', image)
#         c = cv2.waitKey(2)
#         if c == 27:
#             break
#     cv2.waitKey(0)
#     cv2.destroyAllWindows()


def detect(frame):
    # cap = cv2.VideoCapture(0)
    # cap = cv2.VideoCapture("5.mp4")
    # while True:
    #     ret, image = cap.read()
        # image = cv2.flip(image, 1)
        # if ret is False:
        #     break

    # image= motion_detect(image)
    frame = imutils.resize(frame, width=min(800, frame.shape[1]))
    image = face_detect(frame)
    # image =motion_detect(image)
    # cv2.imshow('face-detection-demo', image)

    # cv2.waitKey(2)

    return image

if __name__=="__main__":
    detect()