package ru.checkdev.desc.web;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.checkdev.desc.DescSrv;
import ru.checkdev.desc.domain.Category;
import ru.checkdev.desc.domain.Topic;
import ru.checkdev.desc.dto.TopicLiteDTO;
import ru.checkdev.desc.service.CategoryService;
import ru.checkdev.desc.service.TopicService;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TopicsControl Test
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 09.11.2023
 */
@SpringBootTest(classes = DescSrv.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
class TopicsControlTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TopicService topicService;
    @MockBean
    private CategoryService categoryService;

    @Test
    void injectedNotNull() {
        assertThat(topicService).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void whenGetAllTopicLiteDTOWhenListTopicLite() throws Exception {
        var topicLiteDTO = new TopicLiteDTO(1, "name", "text", 2, "category", 33);
        var topicList = List.of(topicLiteDTO);
        doReturn(topicList).when(topicService).getAllTopicLiteDTO();
        this.mockMvc.perform(get("/topics/dto/lite"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.is(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(topicList.get(0).getId())));
    }

    @Test
    void whenGetAllTopicLiteDTOWhenListEmpty() throws Exception {
        doReturn(Collections.emptyList()).when(topicService).getAllTopicLiteDTO();
        this.mockMvc.perform(get("/topics/dto/lite"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.is(0)));
    }

    @Test
    void whenGetTopicByCategoryIdThenEmptyList() throws Exception {
        var category = new Category();
        category.setId(1);
        doReturn(Collections.emptyList()).when(topicService).findByCategoryId(category.getId());
        mockMvc.perform(get("/topics/{id}", category.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.is(0)));
    }

    @Test
    void whenGetTopicByCategoryIdThenListSizeOne() throws Exception {
        var category = new Category();
        category.setId(1);
        var topic = new Topic(1, "name", "text",
                Calendar.getInstance(), null,
                1, 2, category);
        var topicList = List.of(topic);
        doNothing().when(categoryService).updateStatistic(category.getId());
        doReturn(topicList).when(topicService).findByCategoryId(category.getId());
        mockMvc.perform(get("/topics/{id}", category.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.is(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(topic.getId())));
    }

}