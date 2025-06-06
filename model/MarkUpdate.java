package model;

public class MarkUpdate {
    private final String username;
    private final int mark;

    public MarkUpdate(String username, int mark) {
        this.username = username;
        this.mark = mark;
    }

    public String getUsername() {
        return username;
    }

    public int getMark() {
        return mark;
    }
}