package ru.checkdev.generator.service.vacancy.statistic.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.checkdev.generator.domain.VacancyStatistic;
import ru.checkdev.generator.dto.DirectionKey;
import ru.checkdev.generator.util.parser.Parser;

@Service
@RequiredArgsConstructor
public class HeadHunterStatisticMapper implements StatisticMapper<DirectionKey, VacancyStatistic> {

    private final Parser<Integer> parser;

    @Value("${hh_vacancies_link}")
    private String url;

    @Override
    public VacancyStatistic map(DirectionKey entity) {
        var name = entity.getName();
        return new VacancyStatistic(entity.getId(), entity.getName(),
                parser.parse(String.format("%s%s", url, name)), 0);
    }
}
