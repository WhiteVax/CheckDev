package ru.checkdev.generator.util;

import org.springframework.stereotype.Component;
import ru.checkdev.generator.domain.VacancyStatistic;

import java.util.Comparator;

@Component
public class StatisticCountComparator implements Comparator<VacancyStatistic> {

    @Override
    public int compare(VacancyStatistic vs1, VacancyStatistic vs2) {
        return Integer.compare(vs1.getCount(), vs2.getCount());
    }

    @Override
    public Comparator<VacancyStatistic> reversed() {
        return (vs1, vs2) -> Integer.compare(vs2.getCount(), vs1.getCount());
    }
}
