package ru.job4j.site.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.job4j.site.dto.*;
import ru.job4j.site.util.RestAuthCall;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class NotificationService {

    private static final String SERVICE_ID = "notification";
    private static final String COMMAND_TOPIC = "checkdev.notification.commands";

    private final EurekaUriProvider uriProvider;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void addSubscribeCategory(String token, int userId, int categoryId) {
        publishCommand(NotificationKafkaEventType.ADD_SUBSCRIBE_CATEGORY,
                new SubscribeCategory(userId, categoryId));
    }

    public void deleteSubscribeCategory(String token, int userId, int categoryId) {
        publishCommand(NotificationKafkaEventType.DELETE_SUBSCRIBE_CATEGORY,
                new SubscribeCategory(userId, categoryId));
    }

    public Optional<UserDTO> findCategoriesByUserId(int id) {
        var mapper = new ObjectMapper();
        try {
            var text = new RestAuthCall(String
                    .format("%s/subscribeCategory/%d", uriProvider.getUri(SERVICE_ID), id))
                    .get();
            List<Integer> list = mapper.readValue(text, new TypeReference<>() {
            });
            return Optional.of(new UserDTO(id, list));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void addSubscribeTopic(String token, int userId, int topicId) {
        publishCommand(NotificationKafkaEventType.ADD_SUBSCRIBE_TOPIC,
                new SubscribeTopicDTO(userId, topicId));
    }

    public void deleteSubscribeTopic(String token, int userId, int topicId) {
        publishCommand(NotificationKafkaEventType.DELETE_SUBSCRIBE_TOPIC,
                new SubscribeTopicDTO(userId, topicId));
    }

    public Optional<UserTopicDTO> findTopicByUserId(int id) {
        var mapper = new ObjectMapper();
        try {
            var text = new RestAuthCall(String
                    .format("%s/subscribeTopic/%d", uriProvider.getUri(SERVICE_ID), id))
                    .get();
            List<Integer> list = mapper.readValue(text, new TypeReference<>() {
            });
            return Optional.of(new UserTopicDTO(id, list));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public List<InnerMessageDTO> findBotMessageByUserId(String token, int id) {
        var url = String
                .format("%s/messages/actual/%d", uriProvider.getUri(SERVICE_ID), id);
        var mapper = new ObjectMapper();
        try {
            var text = new RestAuthCall(url).get(token);
            return mapper.readValue(text, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public void notifyAboutInterviewCreation(String token,
                                             CategoryWithTopicDTO categoryAndTopicIds) {
        publishCommand(NotificationKafkaEventType.INTERVIEW_CREATED, categoryAndTopicIds);
    }

    public void sendFeedBackMessage(String token, InnerMessageDTO innerMessage) {
        publishCommand(NotificationKafkaEventType.INNER_MESSAGE, innerMessage);
    }

    public void sendFeedbackNotification(String token,
                                         FeedbackNotificationDTO feedbackNotification) {
        publishCommand(NotificationKafkaEventType.FEEDBACK_NOTIFICATION, feedbackNotification);
    }

    /**
     * Метод отправляет запрос в сервис Notification.
     * Запрос для отправки подписчикам темы о том, что появилось новое интервью.
     *
     * @param token              String
     * @param interviewNotifyDTO InterviewNotifyDTO
     */
    public void sendSubscribeTopic(String token, InterviewNotifyDTO interviewNotifyDTO) {
        publishCommand(NotificationKafkaEventType.INTERVIEW_TOPIC_NOTIFICATION, interviewNotifyDTO);
    }

    /**
     * Метод отправляет запрос в сервис Notification.
     * Запрос для отправки автору собеседования о том что добавился участник.
     *
     * @param token           String
     * @param wisherNotifyDTO WisherNotifyDTO
     */
    public void sendParticipateAuthor(String token, WisherNotifyDTO wisherNotifyDTO) {
        publishCommand(NotificationKafkaEventType.PARTICIPATE_AUTHOR, wisherNotifyDTO);
    }

    /**
     * Метод отправляет запрос в сервис Notification.
     * Запрос для отправки сообщения участнику собеседования о том что автор собеседования
     * удалил собеседование.
     *
     * @param token              String
     * @param cancelInterviewDTO CancelInterviewNotificationDTO
     */
    public void sendParticipateCancelInterview(String token,
                                               CancelInterviewNotificationDTO cancelInterviewDTO) {
        publishCommand(NotificationKafkaEventType.CANCEL_INTERVIEW, cancelInterviewDTO);
    }

    /**
     * Метод отправляет запрос в сервис Notification.
     * Запрос для отправки сообщения участнику собеседования о том что автор собеседования
     * одобрил другого участника.
     *
     * @param token                  String
     * @param wisherDismissedDTOList List<WisherDismissedDTO>
     */
    public void sendParticipantIsDismissed(String token,
                                           List<WisherDismissedDTO> wisherDismissedDTOList) {
        publishCommand(NotificationKafkaEventType.PARTICIPANT_IS_DISMISSED, wisherDismissedDTOList);
    }

    public void approvedWisher(String token, WisherApprovedDTO wisherApprovedDTO) {
        publishCommand(NotificationKafkaEventType.APPROVED_WISHER, wisherApprovedDTO);
    }

    private void publishCommand(NotificationKafkaEventType type, Object payload) {
        try {
            var event = new NotificationKafkaEvent(type, objectMapper.writeValueAsString(payload));
            kafkaTemplate.send(COMMAND_TOPIC, objectMapper.writeValueAsString(event));
        } catch (Exception e) {
            log.error("Kafka notification command {} not sent, error: {}", type, e.getMessage());
        }
    }
}
