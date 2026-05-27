package ru.checkdev.generator.service.vacancy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.checkdev.generator.domain.VacancyStatistic;
import ru.checkdev.generator.repository.VacancyStatisticRepository;
import ru.checkdev.generator.service.vacancy.statistic.HeadHunterVacancyStatisticService;
import ru.checkdev.generator.util.parser.Parser;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class HeadHunterVacancyStatisticServiceTest {

    @MockBean
    private VacancyStatisticRepository repository;

    @MockBean
    private Parser<Integer> parser;

    @Test
    public void checkStuff() {
        assertThat(repository != null, is(true));
        assertThat(parser!= null, is(true));
    }

    @Test
    public void whenItemCreated() {
        var vacancyStatistic = new VacancyStatistic();
        new HeadHunterVacancyStatisticService(repository, parser).createItem(vacancyStatistic);
        verify(repository, times(1)).save(vacancyStatistic);
    }

    @Test
    public void whenItemUpdate() {
        var vacancyStatistic = new VacancyStatistic(1, "", 1, 0);
        new HeadHunterVacancyStatisticService(repository, parser).updateItem(vacancyStatistic);
        verify(repository, times(1)).save(vacancyStatistic);
    }

    @Test
    public void whenGetStatistic() {
        var list =
                new HeadHunterVacancyStatisticService(repository, parser).getStatistic();
        assertThat(list, is(List.of()));
    }

    @Test
    public void whenSaveAll() {
        var statisticList = List.of(
                new VacancyStatistic(0, "A", 1, 0),
                new VacancyStatistic(0, "B", 2, 0));
        new HeadHunterVacancyStatisticService(repository, parser).saveStatistic(statisticList);
        verify(repository, times(1)).saveAll(statisticList);
    }

    @Test
    public void whenDelete() {
        new HeadHunterVacancyStatisticService(repository, parser).delete(1);
        verify(repository, times(1)).deleteById(1);
    }
}
