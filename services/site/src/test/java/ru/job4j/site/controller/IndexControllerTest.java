package ru.job4j.site.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.site.SiteSrv;
import ru.job4j.site.domain.Breadcrumb;
import ru.job4j.site.domain.VacancyStatistic;
import ru.job4j.site.dto.*;
import ru.job4j.site.service.*;
import ru.job4j.site.service.EurekaUriProvider;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * CheckDev пробное собеседование
 * IndexControllerTest тесты на контроллер IndexController
 *
 * @author Dmitry Stepanov
 * @version 24.09.2023 21:50
 */
@SpringBootTest(classes = SiteSrv.class)
@AutoConfigureMockMvc
class IndexControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CategoriesService categoriesService;
    @MockBean
    private TopicsService topicsService;
    @MockBean
    private InterviewsService interviewsService;
    @MockBean
    private AuthService authService;
    @MockBean
    private VacancyStatisticService vacancyStatisticService;
    @MockBean
    private EurekaUriProvider uriProvider;

    @Test
    void injectedNotNull() {
        assertThat(mockMvc).isNotNull();
        assertThat(categoriesService).isNotNull();
        assertThat(topicsService).isNotNull();
        assertThat(interviewsService).isNotNull();
        assertThat(vacancyStatisticService).isNotNull();
    }

    @Test
    void whenGetIndexPageThenReturnIndex() throws Exception {
        var vacancyStatisticList = List.of(
                new VacancyStatistic(1, "Java", 2345, 12));
        var vacancyStatisticWithDates = new VacancyStatisticWithDates(vacancyStatisticList,
                new VacancyStatisticWithDates.Dates(
                        LocalDateTime.of(2024, 4, 22, 12, 0),
                        LocalDateTime.of(2024, 4, 23, 12, 0)));
        when(vacancyStatisticService.getAll()).thenReturn(vacancyStatisticWithDates);
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void whenGetIndexPageExpectModelAttributeThenOk() throws Exception {
        var token = "1410";
        var topicDTO1 = new TopicDTO();
        var profile1 = new ProfileDTO(1, "username1", "experience", 1,
                Calendar.getInstance(), Calendar.getInstance());
        var profile2 = new ProfileDTO(2, "username2", "experience", 2,
                Calendar.getInstance(), Calendar.getInstance());
        topicDTO1.setId(1);
        topicDTO1.setName("topic1");
        var topicDTO2 = new TopicDTO();
        topicDTO2.setId(2);
        topicDTO2.setName("topic2");
        var cat1 = new CategoryDTO(1, "name1");
        var cat2 = new CategoryDTO(2, "name2");
        var listCat = List.of(cat1, cat2);
        var firstInterview = new InterviewDTO(1, 1, 1, "status1", 1, 1,
                "interview1", "description1", "contact1",
                "30.02.2024", "09.10.2023", 1, "author1", 1L, "");
        var secondInterview = new InterviewDTO(2, 1, 1, "status1", 2, 1,
                "interview2", "description2", "contact2",
                "30.02.2024", "09.10.2023", 1, "author2", 1L, "");
        var listInterviews = List.of(firstInterview, secondInterview);
        var vacancyStatisticList = List.of(
                new VacancyStatistic(1, "Java", 2345, 12));
        var vacancyStatisticWithDates = new VacancyStatisticWithDates(vacancyStatisticList,
                new VacancyStatisticWithDates.Dates(
                        LocalDateTime.of(2024, 4, 22, 12, 0),
                        LocalDateTime.of(2024, 4, 23, 12, 0)));
        when(topicsService.getByCategory(cat1.getId())).thenReturn(List.of(topicDTO1));
        when(topicsService.getByCategory(cat2.getId())).thenReturn(List.of(topicDTO2));
        when(topicsService.getAllTopicLiteDTO()).thenReturn(Collections.emptyList());
        when(categoriesService.getMostPopular()).thenReturn(listCat);
        when(interviewsService.getLast()).thenReturn(listInterviews);
        when(authService.findById(1)).thenReturn(profile1);
        when(authService.findById(2)).thenReturn(profile2);
        when(vacancyStatisticService.getAll()).thenReturn(vacancyStatisticWithDates);
        var listBread = List.of(new Breadcrumb("Главная", "/"));
        mockMvc.perform(get("/index/")
                        .sessionAttr("token", token))
                .andDo(print()).andExpectAll(
                        model().attribute("authService", authService),
                        model().attribute("categories", listCat),
                        model().attribute("breadcrumbs", listBread),
                        model().attribute("topicsLiteMap", Collections.emptyMap()),
                        model().attribute("new_interviews", listInterviews),
                        model().attribute("vacancyStatistic", vacancyStatisticList),
                        model().attribute("vacancyStatisticLastUpdateDate", "22.04.2024 12:00"),
                        model().attribute("vacancyStatisticNextUpdateDate", "23.04.2024 12:00"),
                        view().name("index"));
    }
}