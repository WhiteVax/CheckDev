package ru.checkdev.generator.service.vacancy.statistic.mapper;

public interface StatisticMapper<T1, T2> {

    T2 map(T1 entity);
}
