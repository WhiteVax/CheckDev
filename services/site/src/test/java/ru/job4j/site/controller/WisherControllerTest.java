package ru.job4j.site.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.dto.WisherDto;
import ru.job4j.site.dto.WisherNotifyDTO;
import ru.job4j.site.service.InterviewService;
import ru.job4j.site.service.NotificationService;
import ru.job4j.site.service.WisherServiceWebClient;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author Dmitry Stepanov, user Dmitry
 * @since 13.10.2023
 */
@SpringBootTest(classes = WisherController.class)
@AutoConfigureMockMvc
class WisherControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private WisherServiceWebClient wisherService;
    @MockBean
    private InterviewService interviewService;
    @MockBean
    private NotificationService notificationService;

    @Test
    void whenCreateWisherThenReturnRedirect() throws Exception {
        var wisherNotifiDTO = new WisherNotifyDTO(2, "tile", 3, 5, "Вася", "mail");
        var wisher = new WisherDto(0, wisherNotifiDTO.getInterviewId(), wisherNotifiDTO.getUserId(),
                wisherNotifiDTO.getContactBy(), false);
        var token = "1234";
        when(wisherService.saveWisherDto(token, wisher)).thenReturn(true);
        this.mockMvc.perform(post("/wisher/create")
                        .flashAttr("wisherNotifyDTO", wisherNotifiDTO)
                        .sessionAttr("token", token))
                .andDo(print())
                .andExpect(view().name("redirect:/interview/" + wisher.getInterviewId()));
    }

    @Test
    void whenDismissedWisherThenRedirectInterviewDetailPage() throws Exception {
        var token = "1234";
        var interviewDTO = new InterviewDTO(1, 1, 22, "status22", 2, 1,
                "title", "additional", "contactBy",
                null, null, 0, "author", 1L, "");
        var wisherId = 2;
        var wisherUserId = 13;
        doNothing().when(interviewService).updateStatus(token, interviewDTO);
        when(interviewService.getById(token, interviewDTO.getId())).thenReturn(new InterviewDTO());
        this.mockMvc.perform(post("/wisher/dismissed")
                        .sessionAttr("token", token)
                        .param("interviewId", String.valueOf(interviewDTO.getId()))
                        .param("wisherId", String.valueOf(wisherId))
                        .param("wisherUserId", String.valueOf(wisherUserId)))
                .andDo(print())
                .andExpect(view().name("redirect:/interview/" + interviewDTO.getId()));
    }
}