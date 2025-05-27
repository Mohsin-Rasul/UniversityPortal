import face_recognition
import cv2
import os
import pandas as pd
from datetime import datetime

KNOWN_FACES_DIR = "attendance/python/known_faces"
ATTENDANCE_CSV = "attendance/data/attendance.csv"

known_encodings = []
known_names = []

# Load known faces
for filename in os.listdir(KNOWN_FACES_DIR):
    if filename.lower().endswith((".jpg", ".jpeg", ".png")):
        image_path = os.path.join(KNOWN_FACES_DIR, filename)
        image = face_recognition.load_image_file(image_path)
        encoding = face_recognition.face_encodings(image)

        if encoding:
            known_encodings.append(encoding[0])
            name = os.path.splitext(filename)[0]
            known_names.append(name)

# Start camera
video = cv2.VideoCapture(0)
marked = set()

print("Starting facial recognition. Press 'q' to quit.")

while True:
    ret, frame = video.read()
    if not ret:
        break

    small_frame = cv2.resize(frame, (0, 0), fx=0.25, fy=0.25)
    rgb_small_frame = cv2.cvtColor(small_frame, cv2.COLOR_BGR2RGB)

    face_locations = face_recognition.face_locations(rgb_small_frame)
    face_encodings = face_recognition.face_encodings(rgb_small_frame, face_locations)

    for encoding in face_encodings:
        matches = face_recognition.compare_faces(known_encodings, encoding)
        name = "Unknown"

        if True in matches:
            index = matches.index(True)
            name = known_names[index]

            if name not in marked:
                timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                df = pd.DataFrame([[name, timestamp]], columns=["Username", "Timestamp"])
                df.to_csv(ATTENDANCE_CSV, mode='a', header=not os.path.exists(ATTENDANCE_CSV), index=False)
                print(f"{name} marked present at {timestamp}")
                marked.add(name)

    cv2.imshow("Attendance", frame)
    if cv2.waitKey(1) & 0xFF == ord("q"):
        break

video.release()
cv2.destroyAllWindows()