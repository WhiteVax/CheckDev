package ru.checkdev.generator.service;

import java.util.Collection;

public interface ExamService<T, K> {

    Collection<T> create(Collection<K> keys);
}
