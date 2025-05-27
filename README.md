# 🎓 University Performance Management Portal

This is a Java-based desktop application designed for university-level academic management. It allows teachers to record marks (Quiz, Assignment, Mid, Final) and take facial-recognition-based attendance, while students can view their marks and attendance.

---

## 🛠️ Features

### 👨‍🏫 Teacher Dashboard
- Enter marks for:
  - Quiz
  - Assignment
  - Mid
  - Final
- Launch **Facial Recognition** script to mark attendance automatically

### 👨‍🎓 Student Dashboard
- View subject-wise marks with calculated grade
- View attendance history

### 🧠 Additional Features
- Pagination for marks entry to handle large number of students
- CSV-based lightweight storage (easily editable)
- Cross-language integration with Python for facial recognition

---

## 📁 Project Structure

```

📦project-root/
├── data/
│   ├── users.csv          # Registered user info
│   └── marks.csv          # Marks data
│
├── attendance/
│   ├── python/
│   │   ├── recognize\_faces.py     # Python script for facial recognition attendance
│   │   └── dataset/               # Folder to store images
│   └── attendance.csv     # Logs attendance records (username, date, time)
│
├── gui/
│   ├── LoginFrame.java
│   ├── StudentDashboard.java
│   ├── TeacherDashboard.java
│   └── MarksEntryFrame.java
│
├── model/
│   └── User.java
│
├── util/
│   ├── CSVManager.java
│   ├── GradeCalculator.java
│   └── AttendanceManager.java
└── README.md

````

---

## 🔧 Requirements

### Java Side:
- Java 8 or higher
- IntelliJ / Eclipse or any IDE that supports Java Swing

### Python Side (for Attendance):
- [Python 3.10+](https://www.python.org/downloads/)
- Install required Python packages:

```bash
pip install face_recognition opencv-python numpy
````

> ⚠️ Make sure Python is added to system PATH and scripts are executable from Java.

---

## ▶️ How to Run

1. **Clone this repo**:

   ```bash
   git clone https://github.com/your-username/university-portal.git
   cd university-portal
   ```

2. **Run Java project**:

   * Open in IDE and run `LoginFrame.java`

3. **Facial Recognition Attendance**:

   * From Teacher Dashboard, click “Start Attendance”
   * This triggers `attendance/python/recognize_faces.py` to mark attendance in real-time.

---

## 📌 Notes

* Ensure your webcam is enabled for Python facial recognition.
* `users.csv` must contain correct usernames with roles (e.g., student/teacher).
* You can manually edit the CSV files if needed.

---


## 📄 License

This project is open-source and free to use under the [MIT License](LICENSE).

---

## ✨ Contributions

Pull requests are welcome! For major changes, please open an issue first.

---

## 🙋‍♂️ Authors

* Mohsin Rasul — Java & Python Integration

---

```

---

Let me know if you'd like help adding badges, screenshot placeholders, or GitHub Actions integration!
```


