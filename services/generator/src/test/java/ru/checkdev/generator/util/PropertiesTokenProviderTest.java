package ru.checkdev.generator.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.checkdev.generator.GeneratorSrv;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@SpringBootTest(classes = GeneratorSrv.class)
public class PropertiesTokenProviderTest {

    @Test
    void whenGetToken() {
        String expected = "TEST_TOKEN";
        assertThat(new PropertiesTokenProvider().getToken("test_token"), is(expected));
    }

    @Test
    void whenKeyNotExists() {
        assertThat(new PropertiesTokenProvider().getToken("wrong_key"), is(""));
    }

    @Test
    void whenKeyIsNull() {
        assertThat(new PropertiesTokenProvider()
                .getToken(null) == null, is(true));
    }
}
