package ru.checkdev.generator.service.vacancy.statistic;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.checkdev.generator.domain.LastStatisticUpdateDateTime;
import ru.checkdev.generator.dto.VacancyStatisticWithDates;
import ru.checkdev.generator.repository.LastStatisticUpdateTimeRepository;
import ru.checkdev.generator.util.date.TimeProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StatisticUpdateTimeService {

    private final LastStatisticUpdateTimeRepository repository;
    private final TimeProvider<LocalTime> timeProvider;

    public void saveTime(LastStatisticUpdateDateTime time) {
        repository.save(time);
    }

    public VacancyStatisticWithDates.Dates getDates() {
        VacancyStatisticWithDates.Dates result = new VacancyStatisticWithDates.Dates();
        LocalTime scheduledTime = timeProvider.getTime();
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        LocalDate currentDate = now.toLocalDate();
        LocalDate lastUpdateDate;
        LocalDate nextUpdateDate;
        if (currentTime.isAfter(scheduledTime)) {
            lastUpdateDate = currentDate;
            nextUpdateDate = currentDate.plusDays(1);
        } else {
            lastUpdateDate = currentDate.minusDays(1);
            nextUpdateDate = currentDate;
        }
        Optional<LastStatisticUpdateDateTime> optionalLastStatisticUpdateTime =
                repository.findById(1);
        LocalDateTime lastDateTime;
        if (optionalLastStatisticUpdateTime.isPresent()) {
            LocalDateTime lastFromDb = optionalLastStatisticUpdateTime.get().getTime();
            lastDateTime = lastFromDb != null
                    ? lastFromDb
                    : LocalDateTime.of(lastUpdateDate, scheduledTime);
        } else {
            lastDateTime = LocalDateTime.of(lastUpdateDate, scheduledTime);
        }
        result.setLastUpdate(lastDateTime);
        result.setNextUpdate(LocalDateTime.of(nextUpdateDate, scheduledTime));
        return result;
    }
}
