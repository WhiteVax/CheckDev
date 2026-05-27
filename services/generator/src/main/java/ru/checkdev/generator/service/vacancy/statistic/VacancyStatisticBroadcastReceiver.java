package ru.checkdev.generator.service.vacancy.statistic;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.checkdev.generator.domain.LastStatisticUpdateDateTime;
import ru.checkdev.generator.domain.VacancyStatistic;
import ru.checkdev.generator.util.date.TimeProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@AllArgsConstructor
public class VacancyStatisticBroadcastReceiver {

    private final VacancyStatisticService<VacancyStatistic, Integer> service;
    private final StatisticUpdateTimeService timeService;
    private final TimeProvider<LocalTime> timeProvider;

    @Scheduled(cron = "${scheduled.task.cron}")
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void runTask() {
        service.saveStatistic(service.renewStatistic());
        timeService.saveTime(
                new LastStatisticUpdateDateTime(
                1, LocalDateTime.of(LocalDate.now(), timeProvider.getTime())));
    }
}
