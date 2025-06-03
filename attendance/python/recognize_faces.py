import face_recognition
import cv2
import os
import pandas as pd
from datetime import datetime
import argparse # Added for command-line arguments

# Setup argument parser
parser = argparse.ArgumentParser(description="Facial Recognition Attendance System")
parser.add_argument("--section", type=str, required=True, help="Section identifier (e.g., A, B)")
args = parser.parse_args()

CURRENT_SECTION = args.section

KNOWN_FACES_DIR = "attendance/python/known_faces" # Ensure your student images are here
ATTENDANCE_CSV = "attendance/data/attendance.csv" # This file will now have a section column

known_encodings = []
known_names = []

print(f"Loading known faces for attendance in Section {CURRENT_SECTION}...")
# Load known faces
for filename in os.listdir(KNOWN_FACES_DIR):
    if filename.lower().endswith((".jpg", ".jpeg", ".png")):
        image_path = os.path.join(KNOWN_FACES_DIR, filename)
        image = face_recognition.load_image_file(image_path)
        encoding_list = face_recognition.face_encodings(image)

        if encoding_list: # Check if any face was found
            known_encodings.append(encoding_list[0])
            name = os.path.splitext(filename)[0] # Student's username (should match users.csv)
            known_names.append(name)
print(f"Loaded {len(known_names)} known faces.")

# Start camera
video = cv2.VideoCapture(0)
marked_today_this_session = set() # To prevent multiple marks for the same student in this run

print(f"Starting facial recognition for Section {CURRENT_SECTION}. Press 'q' in the video window to quit.")

while True:
    ret, frame = video.read()
    if not ret:
        print("Error: Failed to capture frame from camera.")
        break

    small_frame = cv2.resize(frame, (0, 0), fx=0.25, fy=0.25)
    rgb_small_frame = cv2.cvtColor(small_frame, cv2.COLOR_BGR2RGB)

    face_locations = face_recognition.face_locations(rgb_small_frame)
    face_encodings_in_frame = face_recognition.face_encodings(rgb_small_frame, face_locations)

    for (top, right, bottom, left), face_encoding in zip(face_locations, face_encodings_in_frame):
        matches = face_recognition.compare_faces(known_encodings, face_encoding)
        name = "Unknown"

        if True in matches:
            first_match_index = matches.index(True)
            name = known_names[first_match_index]

            # Check if this person has already been marked in this session
            if name not in marked_today_this_session:
                timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                
                # New CSV format: Username, Timestamp, Section
                df_new_row = pd.DataFrame([[name, timestamp, CURRENT_SECTION]], 
                                          columns=["Username", "Timestamp", "Section"])
                
                # Check if file exists to determine if header is needed
                file_exists = os.path.exists(ATTENDANCE_CSV)
                df_new_row.to_csv(ATTENDANCE_CSV, mode='a', header=not file_exists, index=False)
                
                print(f"Marked: {name} at {timestamp} for Section {CURRENT_SECTION}")
                marked_today_this_session.add(name)
        
        # Scale back up face locations since the frame we detect in was scaled to 1/4 size
        top *= 4
        right *= 4
        bottom *= 4
        left *= 4

        # Draw a box around the face
        cv2.rectangle(frame, (left, top), (right, bottom), (0, 255, 0), 2)
        # Draw a label with a name below the face
        cv2.rectangle(frame, (left, bottom - 35), (right, bottom), (0, 255, 0), cv2.FILLED)
        font = cv2.FONT_HERSHEY_DUPLEX
        cv2.putText(frame, name, (left + 6, bottom - 6), font, 1.0, (255, 255, 255), 1)


    cv2.imshow(f"Attendance - Section {CURRENT_SECTION} (Press 'q' to quit)", frame)
    if cv2.waitKey(1) & 0xFF == ord("q"):
        break

video.release()
cv2.destroyAllWindows()
print("Attendance session ended.")