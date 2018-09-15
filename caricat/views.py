from django.shortcuts import render
from django.http import HttpResponse, JsonResponse
from .frame import div_frames, combine_frames
from .cartoon import Cartoonizer
from .face import extract_face

def mk_ccr(request, video):
	if request.method != "POST":
		return HttpResponse("Only available via POST")
	video = request.POST["video"]
	frame_dir = div_frames(video)
	# Extract faces from each face
	faces = []
	for frame in frame_dir:
		faces.append(extract_face(frame))
	# Make caricatue of all faces
	caric_face = []
	for face in faces:
		caric_face.append(Cartoonizer(caric_face).render())
	# Combine all caricatues to make moving video
	final_vid = combine_frames(caric_face)
	return JsonResponse({"status":200, "video": final_vid}, safe=False)


def aud_resp(request):
	if request.method != "POST":
		return HttpResponse("Only available via POST")
	inp = list(request.POST["input"].strip().split(' '))
	# dont smoke
	if "smoke" in inp:
		return JsonResponse({"status":200, "message": "This is very bad."}, safe=False)
	# ate unhealthy
	if "junk" in inp or "pizza" in inp or "burger" in inp:
		return JsonResponse({"status":200, "message": "This is very bad."}, safe=False)
	# didnt exercise
	if "skip" in inp or "exercise" in inp:
		return JsonResponse({"status":200, "message": "This is very bad."}, safe=False)
	# missed medicines
	if "miss" in inp:
		return JsonResponse({"status":200, "message": "This is very bad."}, safe=False)
	# stressed out/drank
	if "stress" in inp:
		return JsonResponse({"status":200, "message": "This is very bad."}, safe=False)

	return JsonResponse({"status":200, "message":"Good Boy!"}, safe=False)