package ru.checkdev.mock.enums;

import ru.checkdev.mock.domain.FilterProfile;

import java.util.Arrays;
import java.util.List;

public enum FilterProfileStore {

    AUTHOR(1, "Я автор"),
    PARTICIPANT(2, "Я участник"),
    NOT_AUTHOR(3, "Я не автор"),
    NOT_PARTICIPANT(4, "Я не участник"),
    WISHER(5, "Подана заявка");

    private final int id;
    private final String info;

    FilterProfileStore(int id, String info) {
        this.id = id;
        this.info = info;
    }

    public int getId() {
        return id;
    }

    public String getInfo() {
        return info;
    }

    public static List<FilterProfile> getFilterProfiles() {
        return Arrays.stream(FilterProfileStore.values())
                .map(f -> new FilterProfile(f.id, f.info)).toList();
    }
}
