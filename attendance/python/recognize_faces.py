import face_recognition
import cv2
import pandas as pd
from datetime import datetime
import argparse
from pathlib import Path

# --- Configuration ---
# Use pathlib for robust path handling
BASE_DIR = Path(__file__).parent
KNOWN_FACES_DIR = BASE_DIR / "known_faces"
ATTENDANCE_CSV = BASE_DIR.parent / "data" / "attendance.csv"

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
    for file_path in directory.glob("*.jpg"): # More specific glob
        image = face_recognition.load_image_file(file_path)
        encodings = face_recognition.face_encodings(image)
        if encodings:
            known_encodings.append(encodings[0])
            known_names.append(file_path.stem) # Get name from filename
    print(f"Loaded {len(known_names)} known faces.")
    return known_encodings, known_names

# --- Function to mark attendance ---
def mark_attendance(name, section):
    """Appends a new attendance record to the CSV file."""
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    new_record = pd.DataFrame([[name, timestamp, section]], columns=["Username", "Timestamp", "Section"])
    
    # Use 'header=not ATTENDANCE_CSV.exists()' for cleaner header logic
    new_record.to_csv(ATTENDANCE_CSV, mode='a', header=not ATTENDANCE_CSV.exists(), index=False)
    print(f"Marked: {name} at {timestamp} for Section {section}")

# --- Main Application Logic ---
def main():
    known_encodings, known_names = load_known_faces(KNOWN_FACES_DIR)
    video_capture = cv2.VideoCapture(0)
    
    if not video_capture.isOpened():
        print("Error: Could not open video stream.")
        return

    marked_this_session = set()
    print(f"Starting facial recognition for Section {CURRENT_SECTION}. Press 'q' to quit.")

    while True:
        ret, frame = video_capture.read()
        if not ret:
            break

        # Process a smaller frame for performance
        small_frame = cv2.resize(frame, (0, 0), fx=0.25, fy=0.25)
        rgb_small_frame = cv2.cvtColor(small_frame, cv2.COLOR_BGR2RGB)

        # Find all faces in the current frame
        face_locations = face_recognition.face_locations(rgb_small_frame)
        face_encodings = face_recognition.face_encodings(rgb_small_frame, face_locations)

        for face_encoding, face_location in zip(face_encodings, face_locations):
            # *** FIX APPLIED HERE ***
            # Added tolerance=0.5 to make matching stricter. Default is 0.6
            matches = face_recognition.compare_faces(known_encodings, face_encoding, tolerance=0.5)
            name = "Unknown"

            if True in matches:
                first_match_index = matches.index(True)
                name = known_names[first_match_index]

                if name not in marked_this_session:
                    mark_attendance(name, CURRENT_SECTION)
                    marked_this_session.add(name)
            
            # Display the results
            top, right, bottom, left = [v * 4 for v in face_location] # Scale back up
            cv2.rectangle(frame, (left, top), (right, bottom), (0, 255, 0), 2)
            cv2.putText(frame, name, (left + 6, bottom - 6), cv2.FONT_HERSHEY_DUPLEX, 0.8, (255, 255, 255), 1)

        cv2.imshow(f"Attendance - Section {CURRENT_SECTION} (Press 'q' to quit)", frame)

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    video_capture.release()
    cv2.destroyAllWindows()
    print("Attendance session ended.")

if __name__ == "__main__":
    main()