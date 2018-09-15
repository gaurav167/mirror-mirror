from scipy.spatial import distance
from imutils import face_utils
import imutils
import dlib
import cv2
import numpy as np

# def extract_face(Frame):
def eye_aspect_ratio(eye):
	A = distance.euclidean(eye[1], eye[5])
	B = distance.euclidean(eye[2], eye[4])
	C = distance.euclidean(eye[0], eye[3])
	ear = (A + B) / (2.0 * C)
	return ear
	
thresh = 0.25
frame_check = 20
detect = dlib.get_frontal_face_detector()
predict = dlib.shape_predictor(r"/home/alpha/Documents/ml/drowsiness_detection/Drowsiness_Detection/shape_predictor_68_face_landmarks.dat")# Dat file is the crux of the code


(lStart, lEnd) = face_utils.FACIAL_LANDMARKS_68_IDXS["left_eye"]
(rStart, rEnd) = face_utils.FACIAL_LANDMARKS_68_IDXS["right_eye"]
(mStart, mEnd) = face_utils.FACIAL_LANDMARKS_68_IDXS["mouth"]
(nStart, nEnd) = face_utils.FACIAL_LANDMARKS_68_IDXS["nose"]
(jStart, jEnd) = face_utils.FACIAL_LANDMARKS_68_IDXS["jaw"]
print(face_utils.FACIAL_LANDMARKS_68_IDXS)

cap=cv2.VideoCapture(0)
flag=0
while True:
	ret, frame=cap.read()
	frame = imutils.resize(frame, width=450)
	gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
	subjects = detect(gray, 0)

	for subject in subjects:
		shape = predict(gray, subject)
		shape = face_utils.shape_to_np(shape)#converting to NumPy Array
		# print(shape)
		nose = shape[nStart:nEnd]
		leftEye = shape[lStart:lEnd][0]
		rightEye = shape[rStart:rEnd][0]
		m1 = (leftEye[1]-rightEye[1])/(leftEye[0]-rightEye[0])
		c = nose[0][1] - m1*nose[0][0]

		# print(nose[0][1])
		jaw = shape[jStart:jEnd]
		head=[]
		# head = [[i[0], 2*nose[0][1]-i[1]] for i in jaw]
		for i in jaw:
			d = (i[0] + (i[1]-c)*m1)/(1+m1*m1)
			q = int(2*d - i[0])
			p = int(2*d*m1 - i[1] + 2*c)
			head.append([q,p])
		print(head)
		# break
		# head=[]
		# for i in jaw:
		# 	head.append([i[0], 2*nose[0][1]-i[1]])
		# break
		# print(type(jaw), jaw)
		# leftEAR = eye_aspect_ratio(leftEye)
		# rightEAR = eye_aspect_ratio(rightEye)
		# ear = (leftEAR + rightEAR) / 2.0
		
		# leftEyeHull = cv2.convexHull(leftEye)
		# rightEyeHull = cv2.convexHull(rightEye)
		# jawHull = cv2.convexHull(jaw)
		fc = jaw.tolist() + head
		hull = cv2.convexHull(np.array(fc))
		# print(headHull, [headHull])
		# hull = np.append(jawHull, headHull)
		# hull = []
		# for i in jawHull:
		# 	print("i", i)
		# 	hull.append(i)
		# for i in headHull:
		# 	print("i", i)
		# 	hull.append(i)
		# hull = np.array(jawHull.tolist().append(headHull.tolist()))
		# print(hull)
	# break
		# print(type(jawHull), jawHull)
		# print(jawHull[0][0])
		# break
		# print(jaw)
		# headHull = []
		# for i in jaw:
		# 	# print(i[0])
		# 	# print(headHull)
		# 	# print(np.array([[i[0][0], i[0][1]-(2)*(i[0][1]-nose[0][1])]]))
		# 	# headHull.append([[i[0][0], i[0][1]-(2)*(i[0][1]-nose[0][1])]])
		# 	headHull.append([[i[0], i[1]-(2)*(i[1]-nose[0][1])]])
		# print("h", headHull.type)
		# headHull = np.array(headHull)
		# print("j", jawHull.shape)
		# headHull = cv2.convexHull(headHull)
		# hull = np.concatenate(jawHull, np.array(headHull))
	# break
		# noseHull = cv2.convexHull(nose)
		# print(leftEyeHull, leftEyeHull.shape, ear, leftEAR, leftEye)
		# cv2.drawContours(frame, [leftEyeHull], -1, (0, 255, 0), 1)
		# cv2.drawContours(frame, [rightEyeHull], -1, (0, 255, 0), 1)
		mask = np.ones(frame.shape[:2], dtype="uint8") * 0
		cv2.drawContours(mask, [hull], -1, (255,255,255), -1)
		# cv2.drawContours(mask, jawHull, -1, 0, -1)
		image = cv2.bitwise_and(frame, frame, mask=mask)
		# cv2.drawContours(frame, [hull], -1, (0,255,0), 1)
		# cv2.drawContours(frame, [headHull], -1, (0,0,255), 1)
	cv2.imshow("Frame", frame)
	cv2.imshow("Final", image)
	key = cv2.waitKey(1) & 0xFF
	if key == ord("q"):
		break
cv2.destroyAllWindows()
cap.stop()