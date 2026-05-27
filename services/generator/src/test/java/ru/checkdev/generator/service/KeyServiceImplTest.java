package ru.checkdev.generator.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.checkdev.generator.GeneratorSrv;
import ru.checkdev.generator.domain.Key;
import ru.checkdev.generator.repository.KeyRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = GeneratorSrv.class)
class KeyServiceImplTest {
    @MockBean
    private KeyRepository repository;

    @Autowired
    private KeyService service;

    @Test
    void getKeysForExam() {
        Key key1 = new Key(1, "Java");
        Key key2 = new Key(2, "Spring Data");
        Key key3 = new Key(3, "JUnit");
        List<Key> keyListFromRepo = List.of(key1, key2, key3);
        List<Key> keyListFromService = List.of(key1, key2);
        var text = "Стек технологий:\n" +
                "\n" +
                "Java 11\n" +
                "Spring\n" +
                "Spring Data" +
                "Maven/Gradle\n" +
                "Git\n" +
                "BitBucket\n" +
                "Jenkins\n" +
                "SonarQube";
        when(repository.findAll()).thenReturn(keyListFromRepo);
        assertThat(service.getKeysForExam(text), is(keyListFromService));
    }
}