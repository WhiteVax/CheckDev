package ru.checkdev.generator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.checkdev.generator.domain.VacancyStatistic;
import ru.checkdev.generator.dto.DirectionKey;
import ru.checkdev.generator.service.vacancy.statistic.StatisticUpdateTimeService;
import ru.checkdev.generator.service.vacancy.statistic.VacancyStatisticService;
import ru.checkdev.generator.service.vacancy.statistic.mapper.StatisticMapper;
import ru.checkdev.generator.util.StatisticCountComparator;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(StatisticController.class)
public class StatisticControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VacancyStatisticService<VacancyStatistic, Integer> vacancyStatisticService;

    @MockBean
    private StatisticMapper<DirectionKey, VacancyStatistic> mapper;

    @MockBean
    private StatisticCountComparator statisticComparator;

    @MockBean
    private StatisticUpdateTimeService statisticUpdateTimeService;

    @Test
    public void checkStuff() {
        assertThat(mockMvc != null).isTrue();
        assertThat(vacancyStatisticService != null).isTrue();
        assertThat(mapper != null).isTrue();
        assertThat(statisticComparator != null).isTrue();
        assertThat(statisticUpdateTimeService != null).isTrue();
    }

    @Test
    public void whenItemCreated() throws Exception {
        DirectionKey directionKey = new DirectionKey(1, "Example");
        VacancyStatistic statistic = new VacancyStatistic();
        given(mapper.map(directionKey)).willReturn(statistic);
        mockMvc.perform(post("/statistic/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(directionKey)))
                .andExpect(status().isOk());
        verify(vacancyStatisticService, times(1))
                .createItem(statistic);
    }

    @Test
    public void whenGet() throws Exception {
        VacancyStatistic statistic = new VacancyStatistic();
        List<VacancyStatistic> statisticList = new ArrayList<>();
        statisticList.add(statistic);
        given(vacancyStatisticService.getStatistic()).willReturn(statisticList);
        mockMvc.perform(get("/statistic/get"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        verify(vacancyStatisticService, times(1)).getStatistic();
    }

    @Test
    public void whenUpdate() throws Exception {
        DirectionKey directionKey = new DirectionKey(1, "Example");
        VacancyStatistic statistic = new VacancyStatistic();
        given(mapper.map(directionKey)).willReturn(statistic);
        mockMvc.perform(put("/statistic/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(directionKey)))
                .andExpect(status().isOk());
        verify(vacancyStatisticService, times(1)).updateItem(statistic);
    }

    @Test
    public void whenRenew() throws Exception {
        DirectionKey directionKey = new DirectionKey(1, "Example");
        VacancyStatistic statistic = new VacancyStatistic();
        List<VacancyStatistic> statisticList = new ArrayList<>();
        statisticList.add(statistic);
        when(vacancyStatisticService.renewStatistic()).thenReturn(statisticList);
        given(mapper.map(directionKey)).willReturn(statistic);
        mockMvc.perform(get("/statistic/renew")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(vacancyStatisticService, times(1))
                .renewStatistic();
        verify(vacancyStatisticService, times(1))
                .saveStatistic(List.of(statistic));
    }

    @Test
    public void whenTryToRenewWithEmpty() throws Exception {
        DirectionKey directionKey = new DirectionKey(1, "Example");
        VacancyStatistic statistic = new VacancyStatistic();
        List<VacancyStatistic> statisticList = new ArrayList<>();
        when(vacancyStatisticService.renewStatistic()).thenReturn(statisticList);
        given(mapper.map(directionKey)).willReturn(statistic);
        mockMvc.perform(get("/statistic/renew")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(vacancyStatisticService, times(1))
                .renewStatistic();
        verify(vacancyStatisticService, times(1))
                .saveStatistic(List.of());
    }

    @Test
    public void whenDelete() throws Exception {
        int id = 1;
        mockMvc.perform(delete("/statistic/delete/1"))
                .andExpect(status().isNoContent());
        verify(vacancyStatisticService, times(1)).delete(id);
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
