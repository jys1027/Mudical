
from os.path import dirname, join

import numpy as np
import cv2
#from PIL import Image

import tensorflow
from tensorflow import keras






recognizer_filename = join(dirname(__file__), "face_recognizer.xml")
faceCascade = cv2.CascadeClassifier(recognizer_filename)

face_model_name = "face_model_2.1.0"
face_model_filename = join(dirname(__file__), face_model_name)
model = keras.models.load_model(face_model_filename)



def get_emotion(width, height, input_image_string):

    input_image_string = input_image_string[1:-1]

    input_image_list = list(input_image_string.split(", "))

    input_image_list = list(map(int, input_image_list))

    for a in input_image_list:
        if not 0<=a<256:
            return [-1]

    img = np.array(input_image_list)

    img = img.astype(np.uint8)

    img.resize(height, width)



    #return input_image

    #im = Image.fromarray(input_image.astype(np.uint8))
    #im.save("temp.jpeg")



    #img = np.zeros([width,height,3])
    #img[:,:,0] = np.ones([width,height])*64/255.0
    #img[:,:,1] = np.ones([width,height])*128/255.0
    #img[:,:,2] = np.ones([width,height])*192/255.0

    #img = cv2.imread("temp.jpeg")
    #grayImage = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    faces = faceCascade.detectMultiScale(
        img,
        scaleFactor=1.2,
        minNeighbors=5,
        minSize=(20, 20)
    )

    #ret, img = cap.read()
    #img = cv2.flip(img, -1)
    #gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)




    for (x,y,w,h) in faces:

        #cv2.rectangle(img,(x,y),(x+w,y+h),(255,0,0),2)
        roi = img[y:y+h, x:x+w]

        side = max(w, h)
        face_img = np.zeros([side, side])

        for i in range(x, x+side):
            for j in range(y, y+side):
                if i < x+w and j < y+h and i < img.shape[0] and j < img.shape[1]:
                    face_img[i-x][j-y] = img[i][j]
                else:
                    face_img[i-x][j-y] = 0

        face_img = cv2.resize(face_img, dsize=(48, 48), interpolation=cv2.INTER_CUBIC)

        face_img = np.expand_dims(face_img, axis = -1)

        face_img = np.expand_dims(face_img, axis = 0)

        #return face_img.shape

        #face_img[0] = face_img.astype('float32')
        face_img /= 255
        result =  model.predict(face_img)
        result = result[0]
        return result

    return [0]