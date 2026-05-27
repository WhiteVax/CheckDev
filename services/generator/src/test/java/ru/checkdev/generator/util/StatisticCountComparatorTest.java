package ru.checkdev.generator.util;

import org.junit.Test;
import ru.checkdev.generator.domain.VacancyStatistic;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class StatisticCountComparatorTest {

    @Test
    public void whenFirstCountMoreThanSecond() {
        VacancyStatistic first = new VacancyStatistic(1, "A", 2300, 0);
        VacancyStatistic second = new VacancyStatistic(2, "B", 1200, 0);
        assertThat(new StatisticCountComparator().compare(first, second), is(1));
    }

    @Test
    public void whenFirstCountLessThanSecond() {
        VacancyStatistic first = new VacancyStatistic(1, "A", 2300, 0);
        VacancyStatistic second = new VacancyStatistic(2, "B", 3200, 0);
        assertThat(new StatisticCountComparator().compare(first, second), is(-1));
    }

    @Test
    public void whenFirstCountEqualsSecond() {
        VacancyStatistic first = new VacancyStatistic(1, "A", 2300, 0);
        VacancyStatistic second = new VacancyStatistic(2, "B", 2300, 0);
        assertThat(new StatisticCountComparator().compare(first, second), is(0));
    }

    @Test
    public void whenAscSorted() {
        var first = new VacancyStatistic(1, "A", 2300, 0);
        var second = new VacancyStatistic(2, "B", 3200, 0);
        var third = new VacancyStatistic(3, "C", 1400, 0);
        var fourth = new VacancyStatistic(4, "D", 1000, 0);
        var list = new ArrayList<>(List.of(first, second, third, fourth));
        list.sort(new StatisticCountComparator());
        assertThat(list, is(List.of(fourth, third, first, second)));
    }

    @Test
    public void whenDescSorted() {
        var first = new VacancyStatistic(1, "A", 2300, 0);
        var second = new VacancyStatistic(2, "B", 3200, 0);
        var third = new VacancyStatistic(3, "C", 1400, 0);
        var fourth = new VacancyStatistic(4, "D", 1000, 0);
        var list = new ArrayList<>(List.of(first, second, third, fourth));
        list.sort(new StatisticCountComparator().reversed());
        assertThat(list, is(List.of(second, first, third, fourth)));
    }
}
