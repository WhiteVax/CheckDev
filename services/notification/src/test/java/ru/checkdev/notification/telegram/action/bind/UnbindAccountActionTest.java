package ru.checkdev.notification.telegram.action.bind;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.repository.SubscribeTopicRepositoryFake;
import ru.checkdev.notification.repository.UserTelegramRepositoryFake;
import ru.checkdev.notification.service.UserTelegramService;
import ru.checkdev.notification.telegram.SessionTg;
import ru.checkdev.notification.telegram.service.TgCall;

import static org.assertj.core.api.Assertions.assertThat;

class UnbindAccountActionTest {

    private static final Chat CHAT = new Chat(1L, "type");

    private UserTelegramService userTelegramService;
    private UnbindAccountAction unbindAccountAction;

    private SessionTg sessionTg;
    private TgCall tgCall;

    private Update update;
    private Message message;

    @BeforeEach
    void setUp() {
        userTelegramService = new UserTelegramService(
                new UserTelegramRepositoryFake(
                        new SubscribeTopicRepositoryFake()
                )
        );
        sessionTg = Mockito.mock(SessionTg.class);
        tgCall = Mockito.mock(TgCall.class);
        unbindAccountAction = new UnbindAccountAction(
                sessionTg,
                tgCall,
                userTelegramService
        );
        update = new Update();
        message = new Message();
    }

    @Test
    void whenUnbindWithoutUserTelegramThenMessageAccountIsNotBind() {
        message.setChat(CHAT);
        update.setMessage(message);
        String expectMessage =
                "Ваш аккаунт CheckDev отвязан от текущего аккаунта Telegram";
        BotApiMethod<?> botApiMethod =
                unbindAccountAction.handle(update).get();
        SendMessage sendMessage = (SendMessage) botApiMethod;
        assertThat(sendMessage.getText())
                .isEqualTo(expectMessage);
    }

    @Test
    void whenExceptionThenServiceUnavailableMessage() {
        message.setChat(CHAT);
        update.setMessage(message);
        UserTelegram userTelegram =
                new UserTelegram(0, 0, 1L, false);
        userTelegramService.save(userTelegram);
        Mockito.when(sessionTg.get("1", "email", ""))
                .thenReturn("mail@test.com");
        Mockito.when(sessionTg.get("1", "password", ""))
                .thenReturn("123");
        Mockito.when(
                tgCall.doPost(
                        Mockito.anyString(),
                        Mockito.any()
                )
        ).thenThrow(new RuntimeException("Connection error"));
        BotApiMethod<?> botApiMethod =
                unbindAccountAction.handle(update).get();
        SendMessage sendMessage = (SendMessage) botApiMethod;
        assertThat(sendMessage.getText())
                .contains("Сервис недоступен");
    }
}