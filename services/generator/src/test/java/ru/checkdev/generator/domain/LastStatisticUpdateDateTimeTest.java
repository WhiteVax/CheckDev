package ru.checkdev.generator.domain;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class LastStatisticUpdateDateTimeTest {

    @Test
    public void whenEquals() {
        var lastUpdate1 = new LastStatisticUpdateDateTime(1, LocalDateTime.now());
        var lastUpdate2 = new LastStatisticUpdateDateTime(1, LocalDateTime.now().minusDays(1));
        assertThat(lastUpdate1.equals(lastUpdate2), is(true));
        assertThat(lastUpdate1 == lastUpdate2, is(false));
    }

    @Test
    public void whenNotEquals() {
        var lastUpdate1 = new LastStatisticUpdateDateTime(1, LocalDateTime.now());
        var lastUpdate2 = new LastStatisticUpdateDateTime(2, LocalDateTime.now());
        assertThat(lastUpdate1.equals(lastUpdate2), is(false));
    }

    @Test
    public void whenHashCode() {
        var lastUpdate1 = new LastStatisticUpdateDateTime(1, LocalDateTime.now());
        var lastUpdate2 = new LastStatisticUpdateDateTime(2, LocalDateTime.now().minusDays(1));
        assertThat(lastUpdate1.hashCode(), is(32));
        assertThat(lastUpdate2.hashCode(), is(33));
    }
}
