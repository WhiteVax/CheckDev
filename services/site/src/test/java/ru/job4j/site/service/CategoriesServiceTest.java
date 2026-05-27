package ru.job4j.site.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.site.dto.CategoryDTO;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.dto.TopicLiteDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class CategoriesServiceTest {

    private TopicsService topicsService;
    private InterviewsService interviewsService;
    private CategoriesService categoriesService;

    @BeforeEach
    void init() {
        topicsService = mock(TopicsService.class);
        interviewsService = mock(InterviewsService.class);

        categoriesService = spy(
                new CategoriesService(
                        topicsService,
                        interviewsService,
                        mock(EurekaUriProvider.class)
                )
        );
    }

    @Test
    void whenGetMostPopularThenShouldCountInterviewsByCategory() throws Exception {
        var databases = category(10, "Databases");
        var algorithms = category(20, "Algorithms");
        var docker = category(30, "Docker");

        doReturn(List.of(databases, algorithms, docker))
                .when(categoriesService)
                .getPopularFromDesc();

        mockTopics(
                topic(101, 10, "PostgreSQL"),
                topic(102, 10, "Indexes"),
                topic(201, 20, "Sorting"),
                topic(301, 30, "Containers")
        );

        when(interviewsService.getNewInterviews()).thenReturn(List.of(
                interview(5001, 101),
                interview(5002, 102),
                interview(5003, 201)
        ));

        var actual = categoriesService.getMostPopular();

        assertThat(actual)
                .extracting(CategoryDTO::getCountInterview)
                .containsExactly(2L, 1L, 0L);
    }

    @Test
    void whenGetAllWithTopicsThenCategoryWithoutInterviewsShouldHaveZero() throws Exception {
        var devOps = category(40, "DevOps");
        var kotlin = category(50, "Kotlin");

        doReturn(List.of(devOps, kotlin))
                .when(categoriesService)
                .getAll();

        mockTopics(
                topic(401, 40, "CI/CD"),
                topic(501, 50, "Coroutines")
        );

        when(interviewsService.getNewInterviews()).thenReturn(List.of(
                interview(7001, 401)
        ));

        var actual = categoriesService.getAllWithTopics();

        assertThat(actual)
                .extracting(CategoryDTO::getCountInterview)
                .containsExactly(1L, 0L);
    }

    @Test
    void whenInterviewHasUnknownTopicThenShouldIgnoreIt() throws Exception {
        var security = category(60, "Security");

        doReturn(List.of(security))
                .when(categoriesService)
                .getPopularFromDesc();

        mockTopics(
                topic(601, 60, "JWT")
        );

        when(interviewsService.getNewInterviews()).thenReturn(List.of(
                interview(9001, 601),
                interview(9002, 9999)
        ));

        var actual = categoriesService.getMostPopular();

        assertThat(actual.getFirst().getCountInterview()).isEqualTo(1L);
    }

    private void mockTopics(TopicLiteDTO... topics) throws Exception {
        when(topicsService.getAllTopicLiteDTO())
                .thenReturn(List.of(topics));
    }

    private CategoryDTO category(int id, String name) {
        return new CategoryDTO(id, name);
    }

    private TopicLiteDTO topic(int id, int categoryId, String name) {
        return new TopicLiteDTO(
                id,
                name,
                "description",
                categoryId,
                "category",
                1
        );
    }

    private InterviewDTO interview(int id, int topicId) {
        var interview = new InterviewDTO();
        interview.setId(id);
        interview.setTopicId(topicId);
        return interview;
    }
}