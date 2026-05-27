package ru.checkdev.generator.service.vacancy.statistic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.checkdev.generator.domain.VacancyStatistic;
import ru.checkdev.generator.repository.VacancyStatisticRepository;
import ru.checkdev.generator.util.parser.Parser;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class HeadHunterVacancyStatisticService
        implements VacancyStatisticService<VacancyStatistic, Integer> {

    private final VacancyStatisticRepository repository;

    private final Parser<Integer> parser;

    @Value("${hh_vacancies_link}")
    private String url;

    @Override
    public void createItem(VacancyStatistic item) {
        repository.save(item);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateItem(VacancyStatistic item) {
        repository.save(item);
    }

    @Override
    public List<VacancyStatistic> getStatistic() {
        return repository.findAll();
    }

    @Override
    public List<VacancyStatistic> renewStatistic() {
        List<VacancyStatistic> result = new ArrayList<>();
        var oldStatistic = repository.findAll();
        for (var oldElement : oldStatistic) {
            int currentValue = parser.parse(String.format("%s%s", url, oldElement.getName()));
            result.add(new VacancyStatistic(oldElement.getId(),
                    oldElement.getName(), currentValue,
                    currentValue - oldElement.getCount()));
        }
        return result;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void saveStatistic(Collection<VacancyStatistic> statistic) {
        repository.saveAll(statistic);
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
