package ru.checkdev.generator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.checkdev.generator.domain.VacancyStatistic;

public interface VacancyStatisticRepository extends JpaRepository<VacancyStatistic, Integer> {
}
