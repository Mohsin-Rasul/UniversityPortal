### 🎓 University Performance Management Portal

This is a robust, object-oriented desktop application developed in Java Swing, designed to streamline academic management tasks within a university setting. The portal provides distinct interfaces for administrators, teachers, and students, each tailored to their specific roles. It features CSV-based data persistence and integrates with a Python script for modern, facial-recognition-based attendance marking.

-----

### 🌟 Core Features

The application is built around three user roles, providing a secure and intuitive experience for each.

#### 👨‍💼 Administrator Portal

  * **User Management**: Administrators can add, edit, and delete user accounts (students, teachers, and other admins).
  * **Subject & Course Management**: Admins have the authority to create new subjects, assign teachers to them, and manage the master list of courses offered.
  * **Student Enrollment**: Provides a feature to enroll multiple students into a subject at once, which automatically creates the necessary mark sheets.

#### 👨‍🏫 Teacher Dashboard

  * **Class Selection**: Teachers can easily switch between the different subjects they are assigned to.
  * **Flexible Grading Policies**: Teachers can set the grading policy for the entire system, choosing between **Absolute** and **Relative** (curve-based) grading.
  * **Comprehensive Marks Entry**: A dedicated interface allows for entering and updating marks for:
      * 4 Quizzes
      * 4 Assignments
      * Mid-Term Exam
      * Final Exam
  * **Automated Attendance**: Teachers can launch a real-time facial recognition system to mark student attendance for a selected class. The system is designed to prevent duplicate entries within the same class hour.

#### 👨‍🎓 Student Dashboard

  * **Detailed Academic Overview**: Students can view a list of their enrolled subjects and select one to see detailed marks for all assessments.
  * **Calculated Final Grades**: The dashboard automatically calculates the final weighted score and the corresponding letter grade based on the currently active grading policy (Absolute or Relative).
  * **Grade Calculator**: A built-in utility allows students to calculate the score needed on future assessments (e.g., the final exam) to achieve a desired overall course grade.
  * **Attendance Tracking**: Students can view a log of their attendance for each subject, with each entry timestamped.

-----

### 📂 Project Structure

The project is organized into a clean and scalable structure that separates the user interface, data models, and business logic.

```
📦 UniversityPortal/
├── data/
│   ├── users.csv          # Stores user credentials and roles
│   ├── subjects.csv       # Stores subject codes, names, and assigned teachers
│   ├── marks.csv          # Contains detailed marks for each student in each subject
│   └── attendance.csv     # Logs all attendance records from the facial recognition system
│   └── grading_policy.txt # Stores the currently active grading policy
│
├── attendance/
│   └── python/
│       └── recognize_faces.py # Python script for facial recognition
│
├── gui/
│   ├── LoginFrame.java
│   ├── AdminDashboard.java
│   ├── TeacherDashboard.java
│   ├── StudentDashboard.java
│   ├── UserManagementFrame.java
│   ├── SubjectManagementFrame.java
│   └── MarksEntryFrame.java
│
├── model/
│   ├── User.java
│   ├── Subject.java
│   ├── Mark.java
│   └── Student.java
│
├── util/
│   ├── CSVManager.java       # Handles all read/write operations for CSV files
│   ├── GradeCalculator.java  # Contains logic for absolute and relative grading
│   ├── ConfigManager.java    # Manages loading/saving of the grading policy
│   └── AttendanceManager.java# Utility for accessing attendance data
│
└── Main.java                # Main entry point of the application
```

-----

### 🔧 System Requirements

To run this project, you will need the following:

#### **Java Environment:**

  * Java Development Kit (JDK) 8 or higher.
  * An IDE like IntelliJ IDEA or Eclipse.

#### **Python Environment (for Facial Recognition Attendance):**

  * Python 3.7 or higher.
  * The following Python packages must be installed:
    ```bash
    pip install face_recognition opencv-python pandas numpy
    ```
  * Ensure that Python is added to your system's PATH environment variable so it can be executed from the Java application.

-----

### ▶️ How to Run the Application

1.  **Set up the Environment**: Ensure you have a compatible JDK and Python version installed, along with the required Python packages.
2.  **Clone or Download the Project**: Place the project files in a local directory.
3.  **Launch the Application**: Open the project in a Java IDE and run the `Main.java` file. This will open the **Login Window**.
4.  **Log In**: Use the credentials from the `data/users.csv` file to log in.
      * **Admin Login**: `admin` / `0315`
      * **Teacher Login**: `mohsin` / `0315`
      * **Student Login**: Use any student username (e.g., `student001`, `Mohsin Rasul`) with password `student1`.
5.  **Use the Facial Recognition Feature**:
      * Log in as a teacher.
      * Select a class from the dropdown menu.
      * Click "Start Attendance for Selected Class". A window will appear using your webcam to recognize faces and log attendance in `data/attendance.csv`.

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
