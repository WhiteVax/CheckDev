package ru.job4j.site.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.site.domain.Breadcrumb;
import ru.job4j.site.dto.FeedbackDTO;
import ru.job4j.site.dto.FeedbackNotificationDTO;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.service.FeedbackService;
import ru.job4j.site.service.InterviewService;
import ru.job4j.site.service.NotificationService;
import ru.job4j.site.service.ProfilesService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * FeedbackController TEST
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 25.10.2023
 */
@SpringBootTest(classes = FeedbackController.class)
@AutoConfigureMockMvc
class FeedbackControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    InterviewService interviewService;
    @MockBean
    FeedbackService feedbackService;
    @MockBean
    NotificationService notificationService;
    @MockBean
    ProfilesService profilesService;


    @Test
    void injectedIsNotNull() {
        assertThat(mockMvc).isNotNull();
        assertThat(interviewService).isNotNull();
        assertThat(feedbackService).isNotNull();
        assertThat(notificationService).isNotNull();
    }

    @Test
    void whenGetFeedbackFormThenReturnFeedbackPage() throws Exception {
        var interviewDTO = new InterviewDTO(1, 1, 2, "status2", 2, 1,
                "title", "additional", "contactBy",
                null, null, 0, "author", 1L, "");
        var token = "1234";
        when(interviewService.getById(token, interviewDTO.getId())).thenReturn(interviewDTO);
        var breadcrumbs = List.of(
                new Breadcrumb("Главная", "/index"),
                new Breadcrumb("Собеседования", "/interviews/"),
                new Breadcrumb(interviewDTO.getTitle(), "/interview/" + interviewDTO.getId()),
                new Breadcrumb("Отзыв", "/interview/feedback/" + interviewDTO.getId()));
        this.mockMvc.perform(get("/interview/feedback/{id}", interviewDTO.getId())
                        .sessionAttr("token", token))
                .andDo(print())
                .andExpect(model().attribute("interview", interviewDTO))
                .andExpect(model().attribute("breadcrumbs", breadcrumbs))
                .andExpect(status().isOk())
                .andExpect(view().name("interview/feedbackForm"));
    }

    @Test
    void whenGetFeedbackFormOfInterviewGetExceptionThenRedirectStartPage() throws Exception {
        var interviewId = 1;
        var token = "1234";
        when(interviewService.getById(token, interviewId)).thenThrow(new RuntimeException("error"));
        this.mockMvc.perform(get("/interview/feedback/{id}", interviewId)
                        .sessionAttr("token", token))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    void whenPostSaveFeedbackThenRedirectInterviewPage() throws Exception {
        var token = "1234";
        var feedbackDTO = new FeedbackDTO(1, 1, 1,
                1, "text", 5);
        var feedbackNotification = new FeedbackNotificationDTO(
                1,
                "vasya", "interview", 1);
        when(feedbackService.save(token, feedbackDTO, "vasya")).thenReturn(true);
        mockMvc.perform(post("/interview/createFeedback")
                        .flashAttr("feedbackDTO", feedbackDTO)
                        .flashAttr("userId", 1)
                        .flashAttr("submitterId", 2)
                        .flashAttr("agreedWisherId", 1)
                        .flashAttr("interviewTitle", "interview")
                        .sessionAttr("token", token))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/interview/" + feedbackDTO.getInterviewId()));
    }
}