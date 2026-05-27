package ru.checkdev.desc.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.checkdev.desc.DescSrv;
import ru.checkdev.desc.dto.TopicLiteDTO;
import ru.checkdev.desc.service.TopicService;

import java.util.Calendar;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TopicControl TEST
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 10.11.2023
 */
@SpringBootTest(classes = DescSrv.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
class TopicControlTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TopicService topicService;

    @Test
    void injectedNotNull() {
        assertThat(topicService).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void whenGetTopicLiteDtoByIdThenStatusNotFound() throws Exception {
        var anyId = -99;
        when(topicService.getTopicLiteDTOById(anyId)).thenReturn(Optional.empty());
        this.mockMvc.perform(get("/topic/dto/lite/{tId}", anyId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetTopicLiteDtoByIdThenStatusOkAndBody() throws Exception {
        var dateNow = Calendar.getInstance();
        dateNow.set(Calendar.HOUR_OF_DAY, 0);
        var topicLite = new TopicLiteDTO(99, "name", "text", 11, "categoryName", 33);
        doReturn(Optional.of(topicLite)).when(topicService).getTopicLiteDTOById(topicLite.getId());
        this.mockMvc.perform(get("/topic/dto/lite/{tId}", topicLite.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(topicLite.getId()))
                .andExpect(jsonPath("$.name").value(topicLite.getName()));
    }
}