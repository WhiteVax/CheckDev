package ru.checkdev.notification.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.checkdev.notification.domain.InnerMessage;
import ru.checkdev.notification.domain.SubscribeCategory;
import ru.checkdev.notification.domain.SubscribeTopic;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.dto.CategoryWithTopicDTO;
import ru.checkdev.notification.dto.CancelInterviewNotificationDTO;
import ru.checkdev.notification.dto.FeedbackNotificationDTO;
import ru.checkdev.notification.dto.InterviewNotifyDTO;
import ru.checkdev.notification.dto.WisherApprovedDTO;
import ru.checkdev.notification.dto.WisherDismissedDTO;
import ru.checkdev.notification.dto.WisherNotifyDTO;
import ru.checkdev.notification.service.InnerMessageService;
import ru.checkdev.notification.service.MessagesGenerator;
import ru.checkdev.notification.service.NotificationKafkaEvent;
import ru.checkdev.notification.service.NotificationKafkaEventType;
import ru.checkdev.notification.service.NotificationMessage;
import ru.checkdev.notification.service.NotificationMessagesService;
import ru.checkdev.notification.service.SubscribeCategoryService;
import ru.checkdev.notification.service.SubscribeTopicService;
import ru.checkdev.notification.service.UserTelegramService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaListener {

    private static final String COMMAND_TOPIC = "checkdev.notification.commands";

    private final ObjectMapper objectMapper;
    private final SubscribeCategoryService subscribeCategoryService;
    private final SubscribeTopicService subscribeTopicService;
    private final InnerMessageService innerMessageService;
    private final NotificationMessagesService notificationMessagesService;
    private final MessagesGenerator messagesGenerator;
    private final UserTelegramService userTelegramService;
    private final NotificationMessage<UserTelegram, String, InnerMessage> notificationMessage;

    @KafkaListener(topics = COMMAND_TOPIC)
    public void handle(String message) {
        try {
            NotificationKafkaEvent event = objectMapper.readValue(message, NotificationKafkaEvent.class);
            switch (event.getType()) {
                case ADD_SUBSCRIBE_CATEGORY -> addSubscribeCategory(event.getPayload());
                case DELETE_SUBSCRIBE_CATEGORY -> deleteSubscribeCategory(event.getPayload());
                case ADD_SUBSCRIBE_TOPIC -> addSubscribeTopic(event.getPayload());
                case DELETE_SUBSCRIBE_TOPIC -> deleteSubscribeTopic(event.getPayload());
                case INTERVIEW_TOPIC_NOTIFICATION -> sendMessageSubscribeTopic(event.getPayload());
                case INTERVIEW_CREATED -> createMessagesForInterview(event.getPayload());
                case INNER_MESSAGE -> sendInnerMessage(event.getPayload());
                case FEEDBACK_NOTIFICATION -> sendFeedbackNotification(event.getPayload());
                case PARTICIPATE_AUTHOR -> sendMessageSubmitterInterview(event.getPayload());
                case CANCEL_INTERVIEW -> sendMessageCancelInterview(event.getPayload());
                case PARTICIPANT_IS_DISMISSED -> sendParticipantIsDismissed(event.getPayload());
                case APPROVED_WISHER -> sendMessageApprovedWisher(event.getPayload());
                default -> log.warn("Unknown notification event type: {}", event.getType());
            }
        } catch (Exception e) {
            log.error("Failed to process notification kafka message, error: {}", e.getMessage());
        }
    }

    private void addSubscribeCategory(String payload) throws Exception {
        SubscribeCategory subscribeCategory = objectMapper.readValue(payload, SubscribeCategory.class);
        subscribeCategoryService.save(subscribeCategory);
    }

    private void deleteSubscribeCategory(String payload) throws Exception {
        SubscribeCategory subscribeCategory = objectMapper.readValue(payload, SubscribeCategory.class);
        subscribeCategoryService.delete(subscribeCategory);
    }

    private void addSubscribeTopic(String payload) throws Exception {
        SubscribeTopic subscribeTopic = objectMapper.readValue(payload, SubscribeTopic.class);
        subscribeTopicService.save(subscribeTopic);
    }

    private void deleteSubscribeTopic(String payload) throws Exception {
        SubscribeTopic subscribeTopic = objectMapper.readValue(payload, SubscribeTopic.class);
        subscribeTopicService.delete(subscribeTopic);
    }

    private void sendMessageSubscribeTopic(String payload) throws Exception {
        InterviewNotifyDTO interviewNotifyDTO = objectMapper.readValue(payload, InterviewNotifyDTO.class);
        List<UserTelegram> usersTopic = userTelegramService
                .findAllByTopicIdAndUserIdNot(interviewNotifyDTO.getTopicId(),
                        interviewNotifyDTO.getSubmitterId());
        var message = messagesGenerator.getMessageSubscribeTopic(interviewNotifyDTO);
        notificationMessage.sendMessage(usersTopic, message);
    }

    private void createMessagesForInterview(String payload) throws Exception {
        CategoryWithTopicDTO categoryWithTopicDTO = objectMapper.readValue(payload, CategoryWithTopicDTO.class);
        List<Integer> categorySubscribersIds =
                subscribeCategoryService.findUserIdsByCategoryIdExcludeCurrent(
                        categoryWithTopicDTO.getCategoryId(),
                        categoryWithTopicDTO.getSubmitterId());

        List<Integer> topicSubscribersIds =
                subscribeTopicService.findUserIdsByTopicIdExcludeCurrent(
                        categoryWithTopicDTO.getTopicId(),
                        categoryWithTopicDTO.getSubmitterId());

        innerMessageService.saveMessagesForSubscribers(
                categoryWithTopicDTO,
                categorySubscribersIds, topicSubscribersIds);

        notificationMessagesService.sendMessagesToCategorySubscribers(
                categorySubscribersIds,
                categoryWithTopicDTO);
    }

    private void sendInnerMessage(String payload) throws Exception {
        InnerMessage innerMessage = objectMapper.readValue(payload, InnerMessage.class);
        innerMessageService.send(innerMessage);
    }

    private void sendFeedbackNotification(String payload) throws Exception {
        FeedbackNotificationDTO feedbackNotification = objectMapper.readValue(payload, FeedbackNotificationDTO.class);
        notificationMessagesService.sendFeedbackNotification(feedbackNotification);
    }

    private void sendMessageSubmitterInterview(String payload) throws Exception {
        WisherNotifyDTO wisherNotifyDTO = objectMapper.readValue(payload, WisherNotifyDTO.class);
        var message = messagesGenerator.getMessageParticipateWisher(wisherNotifyDTO);
        InnerMessage innerMessage = InnerMessage.of()
                .userId(wisherNotifyDTO.getSubmitterId())
                .text(message)
                .created(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .read(false)
                .interviewId(wisherNotifyDTO.getInterviewId())
                .build();
        innerMessageService.saveMessage(innerMessage);
        userTelegramService
                .findByUserId(wisherNotifyDTO.getSubmitterId())
                .ifPresent(
                        tg -> notificationMessage.sendMessage(tg, message)
                );
    }

    private void sendMessageCancelInterview(String payload) throws Exception {
        CancelInterviewNotificationDTO cancelInterviewDTO =
                objectMapper.readValue(payload, CancelInterviewNotificationDTO.class);
        var message = messagesGenerator.getMessageCancelInterview(cancelInterviewDTO);
        InnerMessage innerMessage = InnerMessage.of()
                .userId(cancelInterviewDTO.getUserId())
                .text(message)
                .created(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .read(false)
                .interviewId(cancelInterviewDTO.getInterviewId())
                .build();
        CompletableFuture.supplyAsync(() -> innerMessageService.saveMessage(innerMessage));
        userTelegramService
                .findByUserId(cancelInterviewDTO.getUserId())
                .ifPresent(
                        tg -> notificationMessage.sendMessage(tg, message)
                );
    }

    private void sendParticipantIsDismissed(String payload) throws Exception {
        List<WisherDismissedDTO> wisherDtoList =
                objectMapper.readValue(payload, new TypeReference<List<WisherDismissedDTO>>() {
                });
        wisherDtoList.forEach(wisher -> {
                    var message = messagesGenerator.getMessageDismissedWisher(wisher);
                    InnerMessage innerMessage = InnerMessage.of()
                            .userId(wisher.getUserId())
                            .text(message)
                            .created(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                            .read(false)
                            .interviewId(wisher.getInterviewId())
                            .build();
                    CompletableFuture.supplyAsync(() -> innerMessageService.saveMessage(innerMessage));
                    userTelegramService
                            .findByUserId(wisher.getUserId())
                            .ifPresent(
                                    tg -> notificationMessage.sendMessage(tg, message)
                            );
                }
        );
    }

    private void sendMessageApprovedWisher(String payload) throws Exception {
        WisherApprovedDTO wisherApprovedDTO = objectMapper.readValue(payload, WisherApprovedDTO.class);
        notificationMessagesService.sendApprovedNotification(wisherApprovedDTO);
    }
}
