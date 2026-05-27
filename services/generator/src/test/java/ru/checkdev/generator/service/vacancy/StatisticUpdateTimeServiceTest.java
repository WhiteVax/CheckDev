package ru.checkdev.generator.service.vacancy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.checkdev.generator.domain.LastStatisticUpdateDateTime;
import ru.checkdev.generator.dto.VacancyStatisticWithDates;
import ru.checkdev.generator.repository.LastStatisticUpdateTimeRepository;
import ru.checkdev.generator.service.vacancy.statistic.StatisticUpdateTimeService;
import ru.checkdev.generator.util.date.TimeProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class StatisticUpdateTimeServiceTest {

    @MockBean
    private LastStatisticUpdateTimeRepository repository;

    @MockBean
    private TimeProvider<LocalTime> timeProvider;

    @Test
    public void checkStuff() {
        assertThat(repository != null, is(true));
        assertThat(timeProvider != null, is(true));
    }

    @Test
    public void whenSave() {
        var lastUpdateDateTime = new LastStatisticUpdateDateTime(1, LocalDateTime.now());
        new StatisticUpdateTimeService(repository, timeProvider)
                .saveTime(lastUpdateDateTime);
        verify(repository, times(1)).save(lastUpdateDateTime);
    }

    @Test
    public void whenGetDates() {
        StatisticUpdateTimeService service = new StatisticUpdateTimeService(repository, timeProvider);
        var lastUpdateTime = LocalTime.of(13, 30);
        var scheduledTime = LocalTime.of(12, 0);
        var lastUpdateDateTime = new LastStatisticUpdateDateTime(
                1, LocalDateTime.of(LocalDate.now(), lastUpdateTime));
        when(timeProvider.getTime()).thenReturn(scheduledTime);
        when(repository.findById(1)).thenReturn(Optional.of(lastUpdateDateTime));
        var result = service.getDates();

        var expectedLast = LocalDateTime.of(LocalDate.now(), lastUpdateTime);

        var expectedNext = LocalTime.now().isAfter(scheduledTime)
                ? LocalDateTime.of(LocalDate.now().plusDays(1), scheduledTime)
                : LocalDateTime.of(LocalDate.now(), scheduledTime);

        assertThat(expectedLast, is(result.getLastUpdate()));

        assertThat(expectedNext, is(result.getNextUpdate()));
    }

    @Test
    public void whenGetDatesWithEmptyFromDb() {
        StatisticUpdateTimeService service = new StatisticUpdateTimeService(repository, timeProvider);
        var scheduledTime = LocalTime.of(12, 0);
        when(timeProvider.getTime()).thenReturn(LocalTime.of(12, 0));
        when(repository.findById(1)).thenReturn(Optional.empty());
        VacancyStatisticWithDates.Dates result = service.getDates();

        var expectedLast = LocalDateTime.of(LocalDate.now(),
                LocalTime.of(12, 0));

        var expectedNext = LocalTime.now().isAfter(scheduledTime)
                ? LocalDateTime.of(LocalDate.now().plusDays(1), scheduledTime)
                : LocalDateTime.of(LocalDate.now(), scheduledTime);

        assertThat(expectedLast, is(result.getLastUpdate()));

        assertThat(expectedNext, is(result.getNextUpdate()));
    }
}
