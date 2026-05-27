package ru.checkdev.generator.service.vacancy.statistic;

import java.util.Collection;
import java.util.List;

public interface VacancyStatisticService<T, ID> {

    void createItem(T item);

    void updateItem(T item);

    List<T> getStatistic();

    List<T> renewStatistic();

    void saveStatistic(Collection<T> statistic);

    void delete(ID id);
}
