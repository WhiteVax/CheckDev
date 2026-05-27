package ru.job4j.site.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.site.SiteSrv;
import ru.job4j.site.domain.Breadcrumb;
import ru.job4j.site.dto.*;
import ru.job4j.site.enums.InterviewMode;
import ru.job4j.site.enums.StatusInterview;
import ru.job4j.site.service.*;
import ru.job4j.site.service.EurekaUriProvider;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.IntStream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static ru.job4j.site.enums.InterviewMode.ANSWER;
import static ru.job4j.site.enums.InterviewMode.ASK;
import static ru.job4j.site.enums.StatusInterview.*;

@SpringBootTest(classes = SiteSrv.class)
@AutoConfigureMockMvc
public class InterviewsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private InterviewsService interviewsService;
    @MockBean
    private AuthService authService;
    @MockBean
    private ProfilesService profilesService;
    @MockBean
    private CategoriesService categoriesService;
    @MockBean
    private FilterService filterService;
    @MockBean
    private TopicsService topicsService;
    @MockBean
    private NotificationService notificationService;
    @MockBean
    private EurekaUriProvider uriProvider;

    @Test
    public void whenShowAllInterviews() throws Exception {
        var token = "1410";
        var id = 1;
        var profile = new ProfileDTO(id, "username", "experience", 1,
                Calendar.getInstance(), Calendar.getInstance());
        var userInfo = new UserInfoDTO();
        userInfo.setId(1);
        var breadcrumbs = List.of(
                new Breadcrumb("Главная", "/index"),
                new Breadcrumb("Собеседования", "/interviews/?page=0&?size=5"));
        List<InterviewDTO> interviews = IntStream.range(0, 3).mapToObj(i -> {
            var interviewDTO = new InterviewDTO();
            interviewDTO.setId(i);
            interviewDTO.setMode(1);
            interviewDTO.setStatusId(1);
            interviewDTO.setSubmitterId(1);
            interviewDTO.setTitle(String.format("Interview_%d", i));
            interviewDTO.setAdditional("Some text");
            interviewDTO.setContactBy("Some contact");
            interviewDTO.setApproximateDate("30.02.2024");
            interviewDTO.setCreateDate("06.10.2023");
            return interviewDTO;
        }).toList();
        List<CategoryDTO> categories = IntStream.range(0, 3).mapToObj(i -> {
            var category = new CategoryDTO();
            category.setId(i);
            category.setName(String.format("category_%d", i));
            category.setPosition(1);
            category.setTotal(100);
            category.setTopicsSize(14);
            return category;
        }).toList();
        List<TopicIdNameDTO> topicIdNameDTOS = IntStream.range(1, 8).mapToObj(
                i -> new TopicIdNameDTO(i, String.format("topic_id_name_%d", i))
        ).toList();
        var filter = new FilterDTO(1, 1, 1, 1, 1, 0);
        var page = new PageImpl<>(interviews);
        var messages = List.of(new InnerMessageDTO(1, id,
                "message", new Timestamp(System.currentTimeMillis()), 0));
        var filterRequestParams = new FilterRequestParams(
                List.of(1), 1, 0, 0, 1, 0, false);
        when(interviewsService.getAllByUserIdRelated(token, 0, 5, userInfo.getId())).thenReturn(page);
        when(interviewsService.getAllWithFilters(filterRequestParams, 0, 5)).thenReturn(page);
        when(authService.userInfo(token)).thenReturn(userInfo);
        when(authService.findById(1)).thenReturn(profile);
        when(profilesService.getProfileById(id)).thenReturn(Optional.of(profile));
        when(categoriesService.getAll()).thenReturn(categories);
        when(filterService.getByUserId(token, userInfo.getId())).thenReturn(filter);
        when(categoriesService.getNameById(categories, 1)).thenReturn(categories.get(1).getName());
        when(topicsService.getNameById(filter.getTopicId())).thenReturn("SOME TOPIC NAME");
        when(topicsService.getTopicIdNameDtoByCategory(id)).thenReturn(topicIdNameDTOS);
        when(topicsService.getAllTopicLiteDTO()).thenReturn(Collections.emptyList());
        when(topicsService.liteDTTOSToMap(Collections.emptyList())).thenReturn(Collections.emptyMap());
        when(notificationService.findBotMessageByUserId(token, id)).thenReturn(messages);
        mockMvc.perform(get("/interviews/")
                        .sessionAttr("token", token)
                        .param("page", "0")
                        .param("size", "5"))
                .andDo(print())
                .andExpectAll(model().attribute("interviewsPage", page),
                        model().attribute("authService", authService),
                        model().attribute("current_page", "interviews"),
                        model().attribute("userInfo", userInfo),
                        model().attribute("breadcrumbs", breadcrumbs),
                        model().attribute("users", Set.of(profile)),
                        model().attribute("categories", categories),
                        model().attribute("categoryName", "category_1"),
                        model().attribute("topicName", "SOME TOPIC NAME"),
                        model().attribute("filter", filter),
                        model().attribute("topics", topicIdNameDTOS),
                        model().attribute("innerMessages", messages),
                        model().attribute("statuses", new StatusInterview[]{
                                IS_NEW, IN_PROGRESS, IS_FEEDBACK, IS_COMPLETED, IS_CANCELED}),
                        model().attribute("modes", new InterviewMode[]{ASK, ANSWER}),
                        model().attribute("modeName", ""),
                        view().name("interview/interviews"));
    }
}