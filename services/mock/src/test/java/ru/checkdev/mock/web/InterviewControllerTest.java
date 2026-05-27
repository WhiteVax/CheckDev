package ru.checkdev.mock.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.checkdev.mock.MockSrv;
import ru.checkdev.mock.domain.Interview;
import ru.checkdev.mock.dto.InterviewDTO;
import ru.checkdev.mock.enums.StatusInterview;
import ru.checkdev.mock.mapper.InterviewMapper;
import ru.checkdev.mock.service.InterviewService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MockSrv.class)
@AutoConfigureMockMvc
class InterviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterviewService service;

    private Interview interview = Interview.of()
            .id(1)
            .mode(2)
            .status(StatusInterview.IS_NEW)
            .submitterId(3)
            .title("test_title")
            .additional("test_additional")
            .contactBy("test_contact_by")
            .approximateDate("test_approximate_date")
            .createDate(
                    Timestamp
                            .valueOf(
                                    LocalDateTime.now()
                                            .truncatedTo(ChronoUnit.MINUTES)))
            .topicId(1)
            .author("author")
            .build();

    private String string = new GsonBuilder()
            .serializeNulls()
            .create()
            .toJson(InterviewMapper.getInterviewDTO(interview));

    @Disabled
    @Test
    @WithMockUser
    public void whenSaveAndGetTheSame() throws Exception {
        var interviewDTO = InterviewMapper.getInterviewDTO(interview);
        when(service.save(any(InterviewDTO.class))).thenReturn(Optional.of(interviewDTO));
        mockMvc.perform(post("/interview/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(interviewDTO)))
                .andDo(print())
                .andExpectAll(status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().string(string));
    }

    @Test
    public void whenSaveAndGetTheIsUnauthorized() throws Exception {
        var interviewDTO = InterviewMapper.getInterviewDTO(interview);
        when(service.save(any(InterviewDTO.class))).thenReturn(Optional.of(interviewDTO));
        mockMvc.perform(post("/interview/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(interviewDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenGetByIdIsCorrect() throws Exception {
        var interviewDTO = InterviewMapper.getInterviewDTO(interview);
        when(service.findById(any(Integer.class))).thenReturn(Optional.of(interviewDTO));
        this.mockMvc.perform(get("/interview/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(interviewDTO.getId())));
    }

    @Test
    public void whenGetByIdIsEmpty() throws Exception {
        when(service.findById(any(Integer.class))).thenReturn(Optional.empty());
        this.mockMvc.perform(get("/interview/1"))
                .andDo(print())
                .andExpectAll(
                        status().is4xxClientError()
                );
    }

    @Disabled
    @Test
    public void whenTryToUpdateIsCorrect() throws Exception {
        when(service.update(any(InterviewDTO.class))).thenReturn(true);
        this.mockMvc.perform(put("/interview/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(interview)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(string));
    }

    @Test
    public void whenTryToUpdateIsUnauthorized() throws Exception {
        when(service.update(any(InterviewDTO.class))).thenReturn(true);
        this.mockMvc.perform(put("/interview/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(interview)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Disabled
    @Test
    public void whenTryToUpdateIsNotCorrect() throws Exception {
        when(service.update(any(InterviewDTO.class))).thenReturn(false);
        this.mockMvc.perform(put("/interview/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(interview)))
                .andDo(print())
                .andExpectAll(
                        status().isNoContent(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().string(string));
    }

    @Disabled
    @Test
    @WithMockUser
    public void whenUpdateStatusThenReturnStatusOk() throws Exception {
        var interviewDTO = InterviewMapper.getInterviewDTO(interview);
        when(service.updateStatus(interviewDTO)).thenReturn(true);
        this.mockMvc.perform(put("/interview/status/")
                        .flashAttr("interviewDTO", interviewDTO))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void whenUpdateStatusThenIsUnauthorized() throws Exception {
        var interviewDTO = InterviewMapper.getInterviewDTO(interview);
        when(service.updateStatus(interviewDTO)).thenReturn(true);
        this.mockMvc.perform(put("/interview/status/")
                        .flashAttr("interviewDTO", interviewDTO))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenUpdateStatusThenReturnStatusNotFound() throws Exception {
        var interviewDTO = InterviewMapper.getInterviewDTO(interview);
        when(service.updateStatus(interviewDTO)).thenReturn(false);
        this.mockMvc.perform(put("/interview/status/")
                        .flashAttr("interviewDTO", interviewDTO))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}