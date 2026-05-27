package ru.checkdev.generator.domain;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class VacancyStatisticTest {

    @Test
    public void whenEquals() {
        var statistic1 = new VacancyStatistic(1, "A", 100, 2);
        var statistic2 = new VacancyStatistic(1, "A", 100, 2);
        assertThat(statistic1.equals(statistic2), is(true));
        assertThat(statistic1 == statistic2, is(false));
    }

    @Test
    public void whenNotEquals() {
        var statistic1 = new VacancyStatistic(1, "A", 100, 2);
        var statistic2 = new VacancyStatistic(2, "B", 100, 2);
        assertThat(statistic1.equals(statistic2), is(false));
    }

    @Test
    public void whenHashCodesSame() {
        var statistic1 = new VacancyStatistic(1, "A", 100, 2);
        var statistic2 = new VacancyStatistic(1, "A", 100, 2);
        assertThat(statistic1.hashCode() == statistic2.hashCode(), is(true));
    }

    @Test
    public void whenHashCodesDifferent() {
        var statistic1 = new VacancyStatistic(1, "A", 100, 2);
        var statistic2 = new VacancyStatistic(2, "A", 100, 2);
        assertThat(statistic1.hashCode() == statistic2.hashCode(), is(false));
    }
}
