package ru.checkdev.generator.service;

import ru.checkdev.generator.domain.Key;

import java.util.List;

public interface KeyService {
    List<Key> getKeysForExam(String text);
}
