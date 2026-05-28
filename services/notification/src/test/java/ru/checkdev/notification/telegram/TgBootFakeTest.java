package ru.checkdev.notification.telegram;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.checkdev.notification.telegram.action.Action;
import ru.checkdev.notification.telegram.action.info.InfoAction;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TgBootFakeTest {
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "Команда не поддерживается! Список доступных команд: \n/start";

    private static final List<String> AVAILABLE_COMMANDS = List.of(
            "/start - Доступные команды",
            "/new - Регистрация нового пользователя",
            "/check - Связанный аккаунт",
            "/bind - Привязать аккаунт CheckDev",
            "/unbind - Отвязать аккаунт CheckDev"
    );

    @Test
    void whenUnknownCommandThenReturnUnknownCommandMessage() {
        var bot = botWithStartAction();
        bot.onUpdateReceived(update("/unknown"));
        assertThat(sentMessage(bot).trim())
                .contains("Команда не поддерживается!")
                .contains("/start");
    }

    @Test
    void whenStartCommandThenReturnAvailableCommands() {
        var bot = botWithStartAction();
        bot.onUpdateReceived(update("/start"));
        assertThat(sentMessage(bot))
                .contains("/start")
                .contains("/new")
                .contains("/check")
                .contains("/bind")
                .contains("/unbind");
    }
    private static CapturingTgBootFake botWithStartAction() {
        return new CapturingTgBootFake(
                Map.of("/start", List.of(new InfoAction(AVAILABLE_COMMANDS)))
        );
    }

    private static String sentMessage(CapturingTgBootFake bot) {
        return ((SendMessage) bot.sent).getText();
    }

    private static Update update(String text) {
        var update = new Update();
        var message = new Message();
        message.setChat(new Chat(1L, "private"));
        message.setText(text);
        update.setMessage(message);
        return update;
    }

    private static class CapturingTgBootFake extends TgBootFake {
        private BotApiMethod<?> sent;
        private CapturingTgBootFake(Map<String, List<Action>> actions) {
            super(actions, "username", "token");
        }
        @Override
        public void send(BotApiMethod msg) {
            sent = msg;
        }
    }
}