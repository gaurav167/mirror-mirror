from scipy.spatial import distance
from imutils import face_utils
import imutils
import dlib
import cv2
import numpy as np

def extract_face(Frame):
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

	# cap=cv2.VideoCapture(0)
	flag=0
	while True:
		# ret, frame=cap.read()
		frame = imutils.resize(frame, width=450)
		gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
		subjects = detect(gray, 0)

		for subject in subjects:
			shape = predict(gray, subject)
			shape = face_utils.shape_to_np(shape)#converting to NumPy Array
			# leftEye = shape[lStart:lEnd]
			# rightEye = shape[rStart:rEnd]

			nose = shape[nStart:nEnd]
			jaw = shape[jStart:jEnd]
			# print(type(jaw), jaw)
			# leftEAR = eye_aspect_ratio(leftEye)
			# rightEAR = eye_aspect_ratio(rightEye)
			# ear = (leftEAR + rightEAR) / 2.0
			
			# leftEyeHull = cv2.convexHull(leftEye)
			# rightEyeHull = cv2.convexHull(rightEye)
			jawHull = cv2.convexHull(jaw)
			# print(type(jawHull), jawHull)
			# print(jawHull[0][0])
			# break
			print(jaw)
			headHull = []
			for i in jaw:
				# print(i[0])
				print(headHull)
				# print(np.array([[i[0][0], i[0][1]-(2)*(i[0][1]-nose[0][1])]]))
				# headHull.append([[i[0][0], i[0][1]-(2)*(i[0][1]-nose[0][1])]])
				headHull.append([[i[0], i[1]-(2)*(i[1]-nose[0][1])]])
			# print("h", headHull.type)
			headHull = np.array(headHull)
			# print("j", jawHull.shape)
			headHull = cv2.convexHull(headHull)
			# hull = np.concatenate(jawHull, np.array(headHull))
		# break
			# noseHull = cv2.convexHull(nose)
			# print(leftEyeHull, leftEyeHull.shape, ear, leftEAR, leftEye)
			# cv2.drawContours(frame, [leftEyeHull], -1, (0, 255, 0), 1)
			# cv2.drawContours(frame, [rightEyeHull], -1, (0, 255, 0), 1)
			mask = np.ones(frame.shape[:2], dtype="uint8") * 255
			cv2.drawContours(mask, headHull, -1, 0, -1)
			image = cv2.bitwise_and(frame, frame, mask=mask)
			# cv2.drawContours(frame, [jawHull], -1, (0,255,0), 1)
			# cv2.drawContours(frame, np.array(headHull), -1, (0,0,255), 1)
		cv2.imshow("Frame", frame)
		cv2.imshow("Final", image)
		key = cv2.waitKey(1) & 0xFF
		if key == ord("q"):
			break
	# cv2.destroyAllWindows()
	# cap.stop()