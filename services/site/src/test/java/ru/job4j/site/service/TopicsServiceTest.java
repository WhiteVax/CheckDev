package ru.job4j.site.service;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import ru.job4j.site.dto.TopicLiteDTO;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Dmitry Stepanov, user Dmitry
 * @since 09.11.2023
 */
class TopicsServiceTest {

    private final InterviewsService interviewsService = mock(InterviewsService.class);
    private final DiscoveryClient discoveryClient = mock(DiscoveryClient.class);
    private final EurekaUriProvider uriProvider = new EurekaUriProvider(discoveryClient);
    private final TopicsService topicsService = new TopicsService(interviewsService, uriProvider);

    @Test
    void injectedNotNull() {
        assertThat(topicsService).isNotNull();
    }

    @Test
    void getAllTopicLiteDTO() {

    }

    @Test
    void whenLiteDTTOSToMapThenMapIsPresent() {
        var topicLiteDTO1 = new TopicLiteDTO(
                1, "name1", "text1", 2, "nameCategory2", 11);
        var topicLiteDTO2 = new TopicLiteDTO(
                2, "name1", "text2", 3, "nameCategory3", 22);
        var topicLiteDTO3 = new TopicLiteDTO(
                3, "name1", "text3", 4, "nameCategory4", 33);
        var expectMap = Map.of(
                topicLiteDTO1.getId(), topicLiteDTO1,
                topicLiteDTO2.getId(), topicLiteDTO2,
                topicLiteDTO3.getId(), topicLiteDTO3
        );
        var liteDTOs = List.of(topicLiteDTO1, topicLiteDTO2, topicLiteDTO3);
        var actualMap = topicsService.liteDTTOSToMap(liteDTOs);
        assertThat(actualMap).isEqualTo(expectMap);
    }

    @Test
    void whenLiteDTTOSToMapThenMapIsEmpty() {
        var actualMap = topicsService.liteDTTOSToMap(Collections.emptyList());
        assertThat(actualMap).isEmpty();
    }
}