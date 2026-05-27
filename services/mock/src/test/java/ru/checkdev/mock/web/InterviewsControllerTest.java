package ru.checkdev.mock.web;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.checkdev.mock.MockSrv;
import ru.checkdev.mock.domain.Interview;
import ru.checkdev.mock.mapper.InterviewMapper;
import ru.checkdev.mock.repository.InterviewRepository;
import ru.checkdev.mock.service.InterviewService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MockSrv.class)
@AutoConfigureMockMvc
class InterviewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterviewService service;

    @MockBean
    private InterviewRepository interviewRepository;

    private Interview interview = Interview.of()
            .id(1)
            .mode(2)
            .submitterId(3)
            .title("test_title")
            .additional("test_additional")
            .contactBy("test_contact_by")
            .approximateDate("test_approximate_date")
            .createDate(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)))
            .topicId(1)
            .build();

    @Test
    public void whenGetAll() throws Exception {
        var page = new PageImpl<>(List.of(interview));
        when(interviewRepository.findAll(PageRequest.of(0, 5)))
                .thenReturn(page);
        when(service.findPaging(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(page.map(InterviewMapper::getInterviewDTO));
        mockMvc.perform(get("/interviews/"))
                .andDo(print())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void whenGetAllByNoFeedbackThenReturnResponseList() throws Exception {
        int submitterId = 1;
        int userWiserId = 2;
        Interview interview = Interview.of()
                .id(1)
                .submitterId(submitterId)
                .build();
        List<Interview> expect = List.of(interview);
        doReturn(expect).when(service).findAllIdByNoFeedback(userWiserId);
        mockMvc.perform(get("/interviews/noFeedback/{uID}", userWiserId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", Matchers.is(expect.get(0).getId())));
    }

    @Test
    void whenGetAllByNoFeedbackThenReturnResponseEmptyList() throws Exception {
        int userWiserId = 2;
        doReturn(Collections.emptyList()).when(service).findAllIdByNoFeedback(userWiserId);
        mockMvc.perform(get("/interviews/noFeedback/{uID}", userWiserId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", Matchers.is(0)));
    }
}