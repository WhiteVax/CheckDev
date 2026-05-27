package ru.job4j.site.enums;

public enum InterviewMode {

    ASK(1, "Сдать"),
    ANSWER(2, "Принять");

    private final int id;
    private final String info;

    InterviewMode(int id, String info) {
        this.id = id;
        this.info = info;
    }

    public int getId() {
        return id;
    }

    public String getInfo() {
        return info;
    }
}
