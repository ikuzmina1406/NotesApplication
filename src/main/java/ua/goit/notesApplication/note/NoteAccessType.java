package ua.goit.notesApplication.note;

public enum NoteAccessType {
    PRIVATE("PRIVATE"),
    PUBLIC("PUBLIC"),
    ;

    private String type;

    NoteAccessType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
