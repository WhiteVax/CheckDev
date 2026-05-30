package ru.job4j.site.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import ru.job4j.site.dto.SubscribeCategory;
import ru.job4j.site.dto.SubscribeTopicDTO;
import ru.job4j.site.dto.InterviewNotifyDTO;
import ru.job4j.site.dto.WisherApprovedDTO;
import ru.job4j.site.dto.WisherDismissedDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceKafkaTest {

    @Mock
    private EurekaUriProvider uriProvider;
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(uriProvider, kafkaTemplate, objectMapper);
    }

    @Test
    void whenAddSubscribeTopicThenSendKafkaCommand() throws Exception {
        notificationService.addSubscribeTopic("token", 3, 7);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(topicCaptor.capture(), payloadCaptor.capture());
        assertThat(topicCaptor.getValue()).isEqualTo("checkdev.notification.commands");

        NotificationKafkaEvent event = objectMapper.readValue(payloadCaptor.getValue(), NotificationKafkaEvent.class);
        assertThat(event.getType()).isEqualTo(NotificationKafkaEventType.ADD_SUBSCRIBE_TOPIC);
        assertThat(objectMapper.readValue(event.getPayload(), SubscribeTopicDTO.class))
                .usingRecursiveComparison()
                .isEqualTo(new SubscribeTopicDTO(3, 7));
    }

    @Test
    void whenAddSubscribeCategoryThenSendKafkaCommand() throws Exception {
        notificationService.addSubscribeCategory("token", 11, 13);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(topicCaptor.capture(), payloadCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo("checkdev.notification.commands");
        NotificationKafkaEvent event = objectMapper.readValue(payloadCaptor.getValue(), NotificationKafkaEvent.class);
        assertThat(event.getType()).isEqualTo(NotificationKafkaEventType.ADD_SUBSCRIBE_CATEGORY);
        assertThat(objectMapper.readValue(event.getPayload(), SubscribeCategory.class))
                .usingRecursiveComparison()
                .isEqualTo(new SubscribeCategory(11, 13));
    }

    @Test
    void whenSendSubscribeTopicThenSendKafkaCommand() throws Exception {
        var dto = new InterviewNotifyDTO(1, 2, "title", 3, "topic", 4, "category");

        notificationService.sendSubscribeTopic("token", dto);

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(anyString(), payloadCaptor.capture());
        NotificationKafkaEvent event = objectMapper.readValue(payloadCaptor.getValue(), NotificationKafkaEvent.class);
        assertThat(event.getType()).isEqualTo(NotificationKafkaEventType.INTERVIEW_TOPIC_NOTIFICATION);
        assertThat(objectMapper.readValue(event.getPayload(), InterviewNotifyDTO.class))
                .usingRecursiveComparison()
                .isEqualTo(dto);
    }

    @Test
    void whenSendParticipantIsDismissedThenSendKafkaCommand() throws Exception {
        List<WisherDismissedDTO> dtoList = List.of(
                new WisherDismissedDTO(1, "interview", 2, "submitter", 3));

        notificationService.sendParticipantIsDismissed("token", dtoList);

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(anyString(), payloadCaptor.capture());
        NotificationKafkaEvent event = objectMapper.readValue(payloadCaptor.getValue(), NotificationKafkaEvent.class);
        assertThat(event.getType()).isEqualTo(NotificationKafkaEventType.PARTICIPANT_IS_DISMISSED);
        assertThat(objectMapper.readValue(event.getPayload(), new TypeReference<List<WisherDismissedDTO>>() {
                }))
                .usingRecursiveComparison()
                .isEqualTo(dtoList);
    }

    @Test
    void whenApprovedWisherThenSendKafkaCommand() throws Exception {
        var dto = new WisherApprovedDTO(1, 2, 3, "interview", "link", "contact");

        notificationService.approvedWisher("token", dto);

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(anyString(), payloadCaptor.capture());
        NotificationKafkaEvent event = objectMapper.readValue(payloadCaptor.getValue(), NotificationKafkaEvent.class);
        assertThat(event.getType()).isEqualTo(NotificationKafkaEventType.APPROVED_WISHER);
        assertThat(objectMapper.readValue(event.getPayload(), WisherApprovedDTO.class))
                .usingRecursiveComparison()
                .isEqualTo(dto);
    }
}
