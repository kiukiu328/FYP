import cv2 as cv
import argparse
import numpy as np
from utils.anchor_generator import generate_anchors
from utils.anchor_decode import decode_bbox
from utils.nms import single_class_non_max_suppression
from PIL import Image, ImageDraw, ImageFont



####################################################################################################################################################################################
def getFaceBox(net, frame, conf_threshold=0.7):
    frameOpencvDnn = frame.copy()
    frameHeight = frameOpencvDnn.shape[0]
    frameWidth = frameOpencvDnn.shape[1]
    blob = cv.dnn.blobFromImage(frameOpencvDnn, 1.0, (300, 300), [104, 117, 123], True, False)
    net.setInput(blob)
    detections = net.forward()
    bboxes = []
    for i in range(detections.shape[2]):
        confidence = detections[0, 0, i, 2]
        if confidence > conf_threshold:
            x1 = int(detections[0, 0, i, 3] * frameWidth)
            y1 = int(detections[0, 0, i, 4] * frameHeight)
            x2 = int(detections[0, 0, i, 5] * frameWidth)
            y2 = int(detections[0, 0, i, 6] * frameHeight)
            bboxes.append([x1, y1, x2, y2])
            cv.rectangle(frameOpencvDnn, (x1, y1), (x2, y2), (0, 255, 0), int(round(frameHeight / 150)), 8)
    return frameOpencvDnn, bboxes

parser = argparse.ArgumentParser(description='Use this script to run age and gender recognition using OpenCV.')
parser.add_argument('--input',
                    help='Path to input image or video file. Skip this argument to capture frames from a camera.')

args = parser.parse_args()

faceProto = "opencv_face_detector.pbtxt"
faceModel = "opencv_face_detector_uint8.pb"

ageProto = "age_deploy.prototxt"
ageModel = "age_net.caffemodel"

genderProto = "gender_deploy.prototxt"
genderModel = "gender_net.caffemodel"


#######################################
#mask
maskProto = "face_mask_detection.prototxt"
maskModel = "face_mask_detection.caffemodel"
#######################################

MODEL_MEAN_VALUES = (78.4263377603, 87.7689143744, 114.895847746)
ageList = ['(0-2)', '(4-6)', '(8-12)', '(15-20)', '(25-32)', '(38-43)', '(48-53)', '(60-100)']
genderList = ['Male', 'Female']
maskList = ['Mask','NoMask']
# Load network
ageNet = cv.dnn.readNet(ageModel, ageProto)
genderNet = cv.dnn.readNet(genderModel, genderProto)
faceNet = cv.dnn.readNet(faceModel, faceProto)

######################################################################################################################################################


# anchor configuration
feature_map_sizes = [[33, 33], [17, 17], [9, 9], [5, 5], [3, 3]]
anchor_sizes = [[0.04, 0.056], [0.08, 0.11], [0.16, 0.22], [0.32, 0.45], [0.64, 0.72]]
anchor_ratios = [[1, 0.62, 0.42]] * 5

# generate anchors
anchors = generate_anchors(feature_map_sizes, anchor_sizes, anchor_ratios)

# for inference , the batch size is 1, the model output shape is [1, N, 4],
# so we expand dim for anchors to [1, anchor_num, 4]
anchors_exp = np.expand_dims(anchors, axis=0)

id2class = {0: 'Mask', 1: 'NoMask'}
colors = ((0, 255, 0), (255, 0 , 0))

def puttext_chinese(img, text, point, color):
    pilimg = Image.fromarray(img)
    draw = ImageDraw.Draw(pilimg)  # 图片上打印汉字
    fontsize = int(min(img.shape[:2])*0.04)
    font = ImageFont.truetype("simhei.ttf", fontsize, encoding="utf-8")
    y = point[1]-font.getsize(text)[1]
    if y <= font.getsize(text)[1]:
        y = point[1]+font.getsize(text)[1]
    draw.text((point[0], y), text, color, font=font)
    img = np.asarray(pilimg)
    return img

def getOutputsNames(net):
    # Get the names of all the layers in the network
    layersNames = net.getLayerNames()
    # Get the names of the output layers, i.e. the layers with unconnected outputs
    return [layersNames[i[0] - 1] for i in net.getUnconnectedOutLayers()]

def inference(net, image, conf_thresh=0.5, iou_thresh=0.4, target_shape=(160, 160), draw_result=True, chinese=False):
    height, width, _ = image.shape
    blob = cv.dnn.blobFromImage(image, scalefactor=1/255.0, size=target_shape)
    net.setInput(blob)
    y_bboxes_output, y_cls_output = net.forward(getOutputsNames(net))
    # remove the batch dimension, for batch is always 1 for inference.
    y_bboxes = decode_bbox(anchors_exp, y_bboxes_output)[0]
    y_cls = y_cls_output[0]
    # To speed up, do single class NMS, not multiple classes NMS.
    bbox_max_scores = np.max(y_cls, axis=1)
    bbox_max_score_classes = np.argmax(y_cls, axis=1)

    # keep_idx is the alive bounding box after nms.
    keep_idxs = single_class_non_max_suppression(y_bboxes, bbox_max_scores, conf_thresh=conf_thresh, iou_thresh=iou_thresh)
    # keep_idxs  = cv2.dnn.NMSBoxes(y_bboxes.tolist(), bbox_max_scores.tolist(), conf_thresh, iou_thresh)[:,0]
    tl = round(0.002 * (height + width) * 0.5) + 1  # line thickness


    for idx in keep_idxs:
        conf = float(bbox_max_scores[idx])
        class_id = bbox_max_score_classes[idx]
        bbox = y_bboxes[idx]
        # clip the coordinate, avoid the value exceed the image boundary.
        xmin = max(0, int(bbox[0] * width))
        ymin = max(0, int(bbox[1] * height))
        xmax = min(int(bbox[2] * width), width)
        ymax = min(int(bbox[3] * height), height)


        if draw_result:
            cv.rectangle(image, (xmin, ymin), (xmax, ymax), colors[class_id], thickness=tl)

            # if chinese:
            #     image = puttext_chinese(image, id2chiclass[class_id], (xmin, ymin), colors[class_id])  ###puttext_chinese
            # else:
            cv.putText(image, "%s: %.2f" % (id2class[class_id], conf), (xmin + 2, ymin - 2),
                    cv.FONT_HERSHEY_SIMPLEX, 0.8, colors[class_id])

            frameFace, bboxes = getFaceBox(faceNet, image)

            if bboxes.__len__() != 0:
                for bbox in bboxes:
                    # print(bbox)
                    face = image[max(0, bbox[1] - 20):min(bbox[3] + 20, image.shape[0] - 1),
                           max(0, bbox[0] - 20):min(bbox[2] + 20, image.shape[1] - 1)]

                    blob = cv.dnn.blobFromImage(face, 1.0, (227, 227), MODEL_MEAN_VALUES, swapRB=False)
                    genderNet.setInput(blob)
                    genderPreds = genderNet.forward()
                    gender = genderList[genderPreds[0].argmax()]
                    # print("Gender Output : {}".format(genderPreds))
                    print("Gender : {}, conf = {:.3f}".format(gender, genderPreds[0].max()))

                    ageNet.setInput(blob)
                    agePreds = ageNet.forward()
                    age = ageList[agePreds[0].argmax()]
                    print("Age Output : {}".format(agePreds))
                    print("Age : {}, conf = {:.3f}".format(age, agePreds[0].max()))

                    label = "{},{}".format(gender, age)
                    cv.putText(image, label, (xmin, ymin), cv.FONT_HERSHEY_SIMPLEX, 0.8,
                               (0, 255, 255), 2,
                               cv.LINE_AA)

                # Read frame
                # t = time.time()
                # hasFrame, frame = cap.read()

                # if not hasFrame:
                #     cv.waitKey()
                #     break

            # if not bboxes:
            #     print("No face Detected, Checking next frame")
            #     continue



    return image

def run_on_video(Net, video_path, conf_thresh=0.5):
    cap = cv.VideoCapture(video_path)
    if not cap.isOpened():
        raise ValueError("Video open failed.")
        return
    status = True
    while status:
        status, img_raw = cap.read()
        if not status:
            print("Done processing !!!")
            break
        img_raw = cv.cvtColor(img_raw, cv.COLOR_BGR2RGB)
        img_raw = inference(Net, img_raw, target_shape=(260, 260), conf_thresh=conf_thresh)
        cv.imshow('image', img_raw[:,:,::-1])
        key= cv.waitKey(10)
        if key==27:
            break
    cv.destroyAllWindows()

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Face Mask Detection")
    parser.add_argument('--proto', type=str, default='models/face_mask_detection.prototxt', help='prototxt path')
    parser.add_argument('--model', type=str, default='models/face_mask_detection.caffemodel', help='model path')
    parser.add_argument('--img-mode', type=int, default=0, help='set 1 to run on image, 0 to run on video.')
    parser.add_argument('--img-path', type=str, default='img/demo2.jpg', help='path to your image.')
    # parser.add_argument('--hdf5', type=str, help='keras hdf5 file')
    args = parser.parse_args()

    Net = cv.dnn.readNet(args.model, args.proto)
    # if args.img_mode:
    #     img = cv.imread(args.img_path)
    #     img = cv.cvtColor(img, cv.COLOR_BGR2RGB)
    #     result = inference(Net, img, target_shape=(260, 260))
    #     cv.namedWindow('detect', cv.WINDOW_NORMAL)
    #     cv.imshow('detect', result[:,:,::-1])
    #     cv.waitKey(0)
    #     cv.destroyAllWindows()
    # else:
    #     video_path = args.video_path
    #     if args.video_path == '0':
    #         video_path = 0
    video_path="5.mp4"
    run_on_video(Net, video_path, conf_thresh=0.5)

