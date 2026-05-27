package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final EurekaUriProvider uriProvider;
    private static final String SERVICE_ID = "notification";

    public void addSubscribeCategory(String token, int userId, int categoryId) {
        SubscribeCategory subscribeCategory = new SubscribeCategory(userId, categoryId);
        var mapper = new ObjectMapper();
        try {
            var url = String
                    .format("%s/subscribeCategory/add", uriProvider.getUri(SERVICE_ID));
            new RestAuthCall(url).post(token, mapper.writeValueAsString(subscribeCategory));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
        }
    }

    public void deleteSubscribeCategory(String token, int userId, int categoryId) {
        SubscribeCategory subscribeCategory = new SubscribeCategory(userId, categoryId);
        var mapper = new ObjectMapper();
        try {
            var url = String
                    .format("%s/subscribeCategory/delete", uriProvider.getUri(SERVICE_ID));
            new RestAuthCall(url).post(token, mapper.writeValueAsString(subscribeCategory));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
        }
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
        SubscribeTopicDTO subscribeTopicDTO = new SubscribeTopicDTO(userId, topicId);
        var mapper = new ObjectMapper();
        try {
            var url = String.format("%s/subscribeTopic/add", uriProvider.getUri(SERVICE_ID));
            new RestAuthCall(url).post(
                    token, mapper.writeValueAsString(subscribeTopicDTO));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
        }
    }

    public void deleteSubscribeTopic(String token, int userId, int topicId) {
        SubscribeTopicDTO subscribeTopic = new SubscribeTopicDTO(userId, topicId);
        var mapper = new ObjectMapper();
        try {
            var url = String
                    .format("%s/subscribeTopic/delete", uriProvider.getUri(SERVICE_ID));
            new RestAuthCall(url).post(
                    token, mapper.writeValueAsString(subscribeTopic));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
        }
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
        var mapper = new ObjectMapper();
        try {
            new RestAuthCall(String
                    .format("%s/messages/newInterview", uriProvider.getUri(SERVICE_ID)))
                    .post(token, mapper.writeValueAsString(categoryAndTopicIds));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
        }
    }

    public void sendFeedBackMessage(String token, InnerMessageDTO innerMessage) {
        var url = String.format("%s/messages/message", uriProvider.getUri(SERVICE_ID));
        var mapper = new ObjectMapper();
        try {
            new RestAuthCall(url).post(
                    token, mapper.writeValueAsString(innerMessage));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
        }
    }

    public void sendFeedbackNotification(String token,
                                         FeedbackNotificationDTO feedbackNotification) {
        var url = String.format("%s/feedback/interview", uriProvider.getUri(SERVICE_ID));
        var mapper = new ObjectMapper();
        try {
            new RestAuthCall(url).post(
                    token, mapper.writeValueAsString(feedbackNotification));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
        }
    }

    /**
     * Метод отправляет запрос в сервис Notification.
     * Запрос для отправки подписчикам темы о том, что появилось новое интервью.
     *
     * @param token              String
     * @param interviewNotifyDTO InterviewNotifyDTO
     * @throws JsonProcessingException Exception
     */
    public void sendSubscribeTopic(String token, InterviewNotifyDTO interviewNotifyDTO) {
        var url = String
                .format("%s/notification/topic/", uriProvider.getUri(SERVICE_ID));
        var mapper = new ObjectMapper();
        try {
            new RestAuthCall(url).post(
                    token, mapper.writeValueAsString(interviewNotifyDTO));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
        }
    }

    /**
     * Метод оправляет запрос в сервис Notification.
     * Запрос для отправки автору собеседования о том что добавился участник.
     *
     * @param token           String
     * @param wisherNotifyDTO WisherNotifyDTO
     */
    public void sendParticipateAuthor(String token, WisherNotifyDTO wisherNotifyDTO) {
        var url = String
                .format("%s/notification/participate/", uriProvider.getUri(SERVICE_ID));
        var mapper = new ObjectMapper();
        try {
            new RestAuthCall(url).post(
                    token, mapper.writeValueAsString(wisherNotifyDTO));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
        }
    }

    /**
     * Метод оправляет запрос в сервис Notification.
     * Запрос для отправки сообщения участнику собеседования о том что автор собеседования
     * удалил собеседование.
     *
     * @param token              String
     * @param cancelInterviewDTO CancelInterviewNotificationDTO
     */
    public void sendParticipateCancelInterview(String token,
                                               CancelInterviewNotificationDTO cancelInterviewDTO) {
        var url = String
                .format("%s/notification/cancelInterview/", uriProvider.getUri(SERVICE_ID));
        var mapper = new ObjectMapper();
        try {
            new RestAuthCall(url).post(
                    token, mapper.writeValueAsString(cancelInterviewDTO));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e);
        }
    }

    /**
     * Метод оправляет запрос в сервис Notification.
     * Запрос для отправки сообщения участнику собеседования о том что автор собеседования
     * одобрил другого участника.
     *
     * @param token                  String
     * @param wisherDismissedDTOList List<WisherDismissedDTO>
     */
    public void sendParticipantIsDismissed(String token,
                                           List<WisherDismissedDTO> wisherDismissedDTOList) {
        var url = String
                .format("%s/notification/participantIsDismissed/", uriProvider.getUri(SERVICE_ID));
        var mapper = new ObjectMapper();
        try {
            new RestAuthCall(url).post(
                    token, mapper.writeValueAsString(wisherDismissedDTOList));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e);
        }
    }

    public void approvedWisher(String token, WisherApprovedDTO wisherApprovedDTO) {
        var url = String
                .format("%s/notificationWisher/approvedWisher/", uriProvider.getUri(SERVICE_ID));
        var mapper = new ObjectMapper();
        try {
            var out = new RestAuthCall(url).post(
                    token, mapper.writeValueAsString(wisherApprovedDTO));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
        }
    }
}