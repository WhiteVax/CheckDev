package ru.checkdev.generator.util.parser;

public interface Parser<T> {

    T parse(String uri);
}
