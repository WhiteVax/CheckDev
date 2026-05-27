package ru.checkdev.generator.util;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.checkdev.generator.GeneratorSrv;
import ru.checkdev.generator.util.date.PropertiesStatisticUpdateTimeProvider;

import java.time.LocalTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@SpringBootTest(classes = GeneratorSrv.class)
public class PropertiesStatisticUpdateTimeProviderTest {

    @Test
    public void whenGetTime() {
        assertThat(new PropertiesStatisticUpdateTimeProvider().getTime(),
                is(LocalTime.of(12, 0)));
    }
}
