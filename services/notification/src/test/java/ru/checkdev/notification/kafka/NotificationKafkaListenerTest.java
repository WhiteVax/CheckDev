package ru.checkdev.notification.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.checkdev.notification.domain.InnerMessage;
import ru.checkdev.notification.domain.SubscribeCategory;
import ru.checkdev.notification.domain.SubscribeTopic;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.dto.CategoryWithTopicDTO;
import ru.checkdev.notification.dto.InterviewNotifyDTO;
import ru.checkdev.notification.dto.WisherApprovedDTO;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationKafkaListenerTest {

    @Mock
    private SubscribeCategoryService subscribeCategoryService;
    @Mock
    private SubscribeTopicService subscribeTopicService;
    @Mock
    private InnerMessageService innerMessageService;
    @Mock
    private NotificationMessagesService notificationMessagesService;
    @Mock
    private MessagesGenerator messagesGenerator;
    @Mock
    private UserTelegramService userTelegramService;
    @Mock
    private NotificationMessage<UserTelegram, String, InnerMessage> notificationMessage;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private NotificationKafkaListener listener;

    @BeforeEach
    void setUp() {
        listener = new NotificationKafkaListener(
                objectMapper,
                subscribeCategoryService,
                subscribeTopicService,
                innerMessageService,
                notificationMessagesService,
                messagesGenerator,
                userTelegramService,
                notificationMessage);
    }

    @Test
    void whenHandleAddSubscribeCategoryThenSaveCategory() throws Exception {
        SubscribeCategory subscribeCategory = new SubscribeCategory(1, 2, 3);
        listener.handle(toKafkaMessage(NotificationKafkaEventType.ADD_SUBSCRIBE_CATEGORY, subscribeCategory));
        verify(subscribeCategoryService).save(subscribeCategory);
    }

    @Test
    void whenHandleDeleteSubscribeTopicThenDeleteTopic() throws Exception {
        SubscribeTopic subscribeTopic = new SubscribeTopic(3, 4, 5);
        listener.handle(toKafkaMessage(NotificationKafkaEventType.DELETE_SUBSCRIBE_TOPIC, subscribeTopic));
        verify(subscribeTopicService).delete(subscribeTopic);
    }

    @Test
    void whenHandleInterviewCreatedThenSaveAndNotifySubscribers() throws Exception {
        CategoryWithTopicDTO dto = new CategoryWithTopicDTO(1, "category", 2, "topic", 3, 4);
        when(subscribeCategoryService.findUserIdsByCategoryIdExcludeCurrent(1, 4)).thenReturn(List.of(11, 12));
        when(subscribeTopicService.findUserIdsByTopicIdExcludeCurrent(2, 4)).thenReturn(List.of(21));
        listener.handle(toKafkaMessage(NotificationKafkaEventType.INTERVIEW_CREATED, dto));
        verify(subscribeCategoryService).findUserIdsByCategoryIdExcludeCurrent(1, 4);
        verify(subscribeTopicService).findUserIdsByTopicIdExcludeCurrent(2, 4);
        verify(innerMessageService).saveMessagesForSubscribers(dto, List.of(11, 12), List.of(21));
        verify(notificationMessagesService).sendMessagesToCategorySubscribers(List.of(11, 12), dto);
    }

    @Test
    void whenHandleInterviewTopicNotificationThenSendMessageToTopicSubscribers() throws Exception {
        InterviewNotifyDTO dto = InterviewNotifyDTO.of()
                .id(1)
                .submitterId(4)
                .title("title")
                .topicId(2)
                .topicName("topic")
                .categoryId(3)
                .categoryName("category")
                .build();
        List<UserTelegram> users = List.of(new UserTelegram(1, 2, 10L, true));
        when(userTelegramService.findAllByTopicIdAndUserIdNot(2, 4)).thenReturn(users);
        when(messagesGenerator.getMessageSubscribeTopic(dto)).thenReturn("message");
        when(notificationMessage.sendMessage(users, "message")).thenReturn(List.of());
        listener.handle(toKafkaMessage(NotificationKafkaEventType.INTERVIEW_TOPIC_NOTIFICATION, dto));
        verify(userTelegramService).findAllByTopicIdAndUserIdNot(2, 4);
        verify(messagesGenerator).getMessageSubscribeTopic(dto);
        verify(notificationMessage).sendMessage(users, "message");
    }

    @Test
    void whenHandleParticipateAuthorThenSaveMessageAndNotifyAuthor() throws Exception {
        WisherNotifyDTO dto = WisherNotifyDTO.of()
                .interviewId(1)
                .interviewTitle("title")
                .submitterId(2)
                .userId(3)
                .userName("user")
                .contactBy("mail")
                .build();
        when(messagesGenerator.getMessageParticipateWisher(dto)).thenReturn("participate");
        when(userTelegramService.findByUserId(2)).thenReturn(Optional.of(new UserTelegram(1, 2, 10L, true)));
        listener.handle(toKafkaMessage(NotificationKafkaEventType.PARTICIPATE_AUTHOR, dto));
        verify(messagesGenerator).getMessageParticipateWisher(dto);
        verify(innerMessageService).saveMessage(any(InnerMessage.class));
        verify(userTelegramService).findByUserId(2);
        verify(notificationMessage).sendMessage(any(UserTelegram.class), anyString());
    }

    @Test
    void whenHandleApprovedWisherThenDelegateToNotificationMessagesService() throws Exception {
        WisherApprovedDTO dto = new WisherApprovedDTO(1, 2, 3, "title", "link", "contact");
        listener.handle(toKafkaMessage(NotificationKafkaEventType.APPROVED_WISHER, dto));
        verify(notificationMessagesService).sendApprovedNotification(dto);
    }

    private String toKafkaMessage(NotificationKafkaEventType type, Object payload) throws Exception {
        return objectMapper.writeValueAsString(new NotificationKafkaEvent(type,
                objectMapper.writeValueAsString(payload)));
    }
}
