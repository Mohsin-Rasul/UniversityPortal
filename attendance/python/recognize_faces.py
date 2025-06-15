import face_recognition
import cv2
import pandas as pd
from datetime import datetime
import argparse
from pathlib import Path

# --- Configuration ---
BASE_DIR = Path(__file__).resolve().parent
KNOWN_FACES_DIR = BASE_DIR / "known_faces"
ATTENDANCE_CSV = BASE_DIR.parent.parent / "data" / "attendance.csv"

# --- Argument Parsing ---
parser = argparse.ArgumentParser(description="Facial Recognition Attendance System")
parser.add_argument("--section", type=str, required=True, help="Section identifier (e.g., A, B)")
args = parser.parse_args()
CURRENT_SECTION = args.section

# --- Function to load known faces ---
def load_known_faces(directory):
    """Loads face encodings and names from a directory."""
    known_encodings = []
    known_names = []
    print(f"Loading known faces from {directory}...")
    if not directory.exists():
        print(f"Error: Known faces directory not found at {directory}")
        return known_encodings, known_names
        
    for file_path in directory.glob("*.jpg"):
        image = face_recognition.load_image_file(file_path)
        encodings = face_recognition.face_encodings(image)
        if encodings:
            known_encodings.append(encodings[0])
            known_names.append(file_path.stem)
    print(f"Loaded {len(known_names)} known faces.")
    return known_encodings, known_names

# --- Function to get the last time each student was marked ---
def get_last_attendance_times(filepath):
    """Reads the attendance CSV and returns a dict of username -> last_timestamp."""
    if not filepath.exists():
        return {}
    
    try:
        df = pd.read_csv(filepath)
        if df.empty:
            return {}
        
        df['Timestamp'] = pd.to_datetime(df['Timestamp'])
        last_times = df.groupby('Username')['Timestamp'].max().to_dict()
        return last_times
    except (FileNotFoundError, pd.errors.EmptyDataError):
        return {}

# --- Function to mark attendance ---
def mark_attendance(name, section):
    """Appends a new attendance record and returns the timestamp."""
    timestamp = datetime.now()
    new_record = pd.DataFrame([[name, timestamp.strftime("%Y-%m-%d %H:%M:%S"), section]], 
                              columns=["Username", "Timestamp", "Section"])
    
    new_record.to_csv(ATTENDANCE_CSV, mode='a', header=not ATTENDANCE_CSV.exists(), index=False)
    print(f"Marked: {name} at {timestamp.strftime('%H:%M:%S')} for Section {section}")
    return timestamp

# --- Main Application Logic ---
def main():
    known_encodings, known_names = load_known_faces(KNOWN_FACES_DIR)
    if not known_encodings:
        print("No known faces loaded. Exiting.")
        return
        
    video_capture = cv2.VideoCapture(0)
    if not video_capture.isOpened():
        print("Error: Could not open video stream.")
        return

    last_marked_times = get_last_attendance_times(ATTENDANCE_CSV)
    
    print(f"Starting facial recognition for Section {CURRENT_SECTION}. Press 'q' to quit.")

    while True:
        ret, frame = video_capture.read()
        if not ret:
            break
        
        now = datetime.now()
        status_text = ""
        status_color = (0, 255, 0) # Green for active

        # --- CLASS HOUR LOGIC ---
        # 1. Check if the marking window is open (xx:00 to xx:50)
        if now.minute > 50:
            status_text = "Marking Window: CLOSED"
            status_color = (0, 0, 255) # Red for closed
        else:
            status_text = f"Marking Window: OPEN (until {now.hour}:50)"
            
            small_frame = cv2.resize(frame, (0, 0), fx=0.25, fy=0.25)
            rgb_small_frame = cv2.cvtColor(small_frame, cv2.COLOR_BGR2RGB)

            face_locations = face_recognition.face_locations(rgb_small_frame)
            face_encodings = face_recognition.face_encodings(rgb_small_frame, face_locations)

            for face_encoding, face_location in zip(face_encodings, face_locations):
                matches = face_recognition.compare_faces(known_encodings, face_encoding, tolerance=0.5)
                name = "Unknown"

                if True in matches:
                    first_match_index = matches.index(True)
                    name = known_names[first_match_index]

                    # 2. Check if the student has already been marked for this hour
                    can_mark = False
                    last_marked_time = last_marked_times.get(name)
                    
                    if not last_marked_time:
                        can_mark = True # Never marked before
                    else:
                        # Check if the last mark was in a different hour on a different day OR a different hour on the same day
                        is_same_day = last_marked_time.date() == now.date()
                        is_same_hour = last_marked_time.hour == now.hour
                        if not (is_same_day and is_same_hour):
                            can_mark = True

                    if can_mark:
                        new_timestamp = mark_attendance(name, CURRENT_SECTION)
                        last_marked_times[name] = new_timestamp
                
                # Display face rectangle and name
                top, right, bottom, left = [v * 4 for v in face_location]
                cv2.rectangle(frame, (left, top), (right, bottom), (0, 255, 0), 2)
                cv2.putText(frame, name, (left + 6, bottom - 6), cv2.FONT_HERSHEY_DUPLEX, 0.8, (255, 255, 255), 1)

        # Display the overall status on the top-left of the frame
        cv2.putText(frame, status_text, (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.7, status_color, 2)
        cv2.imshow(f"Attendance - Section {CURRENT_SECTION} (Press 'q' to quit)", frame)

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    video_capture.release()
    cv2.destroyAllWindows()
    print("Attendance session ended.")

if __name__ == "__main__":
    main()