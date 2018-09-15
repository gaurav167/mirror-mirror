import cv2
import numpy as np

def div_frames(video):
	path = '/home/alpha/Documents/hackathons/web/caricat/'
	vidcap = cv2.VideoCapture(path+'VID_20180915_181752.mp4')
	success,image = vidcap.read()
	count = 0
	fr = 1
	print(success)
	while success:
		print(fr, fr%15)
		if fr%15==0:
			cv2.imwrite("/home/alpha/Documents/hackathons/web/caricat/frames/frame%d.jpg" % count, image)     # save frame as JPEG file      
		success,image = vidcap.read()
		print('Read a new frame: ', success)
		count += 1
		fr+=1
	return path


def combine_frames(frames):
	nIMAGES = len(frames)
	files = glob.glob(DIR + '\\' + tpname +'\\*.jpeg' )
	image_stack = np.empty((500, 220, nIMAGES))
	mov = DIR + '\\' + tpname + '\\' + tpname + '_mov.gif'
	MOV = cv2.VideoWriter(filename=mov, fourcc=cv2.VideoWriter_fourcc('F', 'M', 'P', '4'), fps=2, frameSize=(220, 500)) # frame size is 220 x 500

	for i in np.arange(0, nIMAGES):
	    print('Working on: ' + files[i][-14:-4])
	    image = cv2.imread(files[i], 0)
	    crop_image = image[50:550, 252:472] #crop y:h, x:w
	    crop_image = cv2.applyColorMap(crop_image, cv2.COLORMAP_JET)
	    MOV.write(crop_image)
	# MOV.release()
	return MOV