package ru.checkdev.generator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.checkdev.generator.domain.LastStatisticUpdateDateTime;

public interface LastStatisticUpdateTimeRepository
        extends JpaRepository<LastStatisticUpdateDateTime, Integer> {
}
