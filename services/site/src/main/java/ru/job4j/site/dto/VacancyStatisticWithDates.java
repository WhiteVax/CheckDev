package ru.job4j.site.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.job4j.site.domain.VacancyStatistic;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VacancyStatisticWithDates {

    private List<VacancyStatistic> statisticList;
    private Dates dates;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Dates {

        private LocalDateTime lastUpdate;
        private LocalDateTime nextUpdate;
    }
}
