package ru.checkdev.generator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.checkdev.generator.domain.LastStatisticUpdateDateTime;
import ru.checkdev.generator.domain.VacancyStatistic;
import ru.checkdev.generator.dto.DirectionKey;
import ru.checkdev.generator.dto.VacancyStatisticWithDates;
import ru.checkdev.generator.service.vacancy.statistic.StatisticUpdateTimeService;
import ru.checkdev.generator.service.vacancy.statistic.VacancyStatisticService;
import ru.checkdev.generator.service.vacancy.statistic.mapper.StatisticMapper;
import ru.checkdev.generator.util.StatisticCountComparator;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/statistic")
@RequiredArgsConstructor
public class StatisticController {

    private final VacancyStatisticService<VacancyStatistic, Integer> vacancyStatisticService;
    private final StatisticMapper<DirectionKey, VacancyStatistic> mapper;
    private final StatisticCountComparator statisticComparator;
    private final StatisticUpdateTimeService statisticUpdateTimeService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    public void create(@RequestBody DirectionKey directionKey) {
        var name = directionKey.getName();
        vacancyStatisticService.createItem(mapper.map(directionKey));
    }

    @GetMapping("/get")
    public ResponseEntity<VacancyStatisticWithDates> getAll() {
        var vacancyStatisticList = vacancyStatisticService.getStatistic();
        vacancyStatisticList.sort(statisticComparator.reversed());
        VacancyStatisticWithDates result =
                new VacancyStatisticWithDates(vacancyStatisticList,
                        statisticUpdateTimeService.getDates());
        return ResponseEntity.status(vacancyStatisticList.size() > 0
                        ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON).body(result);
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public void update(@RequestBody DirectionKey directionKey) {
        vacancyStatisticService.updateItem(mapper.map(directionKey));
    }

    @GetMapping("/renew")
    public ResponseEntity<VacancyStatisticWithDates> renew() {
        var vacancyStatisticList = vacancyStatisticService.renewStatistic();
        vacancyStatisticService.saveStatistic(vacancyStatisticList);
        statisticUpdateTimeService.saveTime(
                new LastStatisticUpdateDateTime(1, LocalDateTime.now()));
        vacancyStatisticList.sort(statisticComparator.reversed());
        VacancyStatisticWithDates result =
                new VacancyStatisticWithDates(vacancyStatisticList,
                        statisticUpdateTimeService.getDates());
        return ResponseEntity.status(vacancyStatisticList.size() > 0
                        ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON).body(result);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        vacancyStatisticService.delete(id);
    }
}
