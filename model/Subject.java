package model;

public class Subject {
    private String code;
    private String name;
    private String teacherUsername; // ADDED: Field to store the teacher's username

    /**
     * MODIFIED: Constructor to accept the teacher's username.
     * @param code The subject code.
     * @param name The subject name.
     * @param teacherUsername The username of the assigned teacher.
     */
    public Subject(String code, String name, String teacherUsername) {
        this.code = code;
        this.name = name;
        this.teacherUsername = teacherUsername;
    }

    // ADDED: Getter and setter for the new field.
    public String getTeacherUsername() {
        return teacherUsername;
    }

    public void setTeacherUsername(String teacherUsername) {
        this.teacherUsername = teacherUsername;
    }

    // --- Existing methods ---
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }
}