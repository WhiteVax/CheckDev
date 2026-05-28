package ru.checkdev.notification.telegram.action.bind;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.checkdev.notification.domain.Profile;
import ru.checkdev.notification.dto.ProfileTgDTO;
import ru.checkdev.notification.service.UserTelegramService;
import ru.checkdev.notification.telegram.SessionTg;
import ru.checkdev.notification.telegram.action.Action;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgCall;
import java.util.Optional;

/**
 * Команда телеграм бота /unbind -
 * отвязать аккаунт CheckDev от текущего аккаунта Telegram
 */

@Slf4j
@AllArgsConstructor
public class UnbindAccountAction implements Action {
    private static final String URL_PROFILE_BY_EMAIL_AND_PASS = "/profiles/tg/byEmailAndPassword";
    private final TgConfig tgConfig = new TgConfig(0);
    private final SessionTg sessionTg;
    private final TgCall tgCall;
    private final UserTelegramService userTelegramService;

    @Override
    public Optional<BotApiMethod> handle(Update update) {
        var chatId = update.getMessage().getChatId();
        var text = "";
        var ls = System.lineSeparator();
        var user = userTelegramService.findByChatId(chatId);
        if (user.isEmpty()) {
            text = "Ваш аккаунт CheckDev отвязан от текущего аккаунта Telegram";
            return Optional.of(new SendMessage(chatId.toString(), text));
        }
        var profile = new Profile();
        profile.setEmail(sessionTg.get(chatId.toString(), "email", ""));
        profile.setPassword(sessionTg.get(chatId.toString(), "password", ""));
        try {
            var result = tgCall.doPost(URL_PROFILE_BY_EMAIL_AND_PASS, profile).block();
            if (result == null) {
                text = "Пользователь не найден";
            } else {
                var profileTg = tgConfig.getMapper().convertValue(result, ProfileTgDTO.class);
                if (profileTg.getId() == user.get().getUserId()) {
                    userTelegramService.delete(user.get());
                    text = "Ваш аккаунт CheckDev отвязан от текущего аккаунта Telegram";
                } else {
                    text = "Введенные данные не соответствуют аккаунту";
                }
            }
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            text = String.format("Сервис недоступен, попробуйте позже%s%s", ls, "/start");
        }
        return Optional.of(new SendMessage(chatId.toString(), text));
    }
}
