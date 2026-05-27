package ru.checkdev.generator.component;

import java.util.Set;

public interface SemanticFetcher {

    public Set<String> fetch(String text);
}
