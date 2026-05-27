package ru.checkdev.generator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.checkdev.generator.domain.Key;

public interface KeyRepository extends JpaRepository<Key, Integer> {
}
