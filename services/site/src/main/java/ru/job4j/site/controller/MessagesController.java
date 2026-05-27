package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.site.dto.InnerMessageDTO;
import ru.job4j.site.service.AuthService;
import ru.job4j.site.service.NotificationService;
import ru.job4j.site.util.RequestResponseTools;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.job4j.site.util.RequestResponseTools.getToken;

/**
 * CheckDev пробное собеседование
 * MessagesController класс обработки запросов внутренних сообщений.
 *
 * @author Avetis Mkhitaryants
 * @version 13.11.2023T16:30
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/messages")
@Slf4j
public class MessagesController {
    private final AuthService authService;
    private final NotificationService notifications;

    @GetMapping("/")
    public String getMessagesByUserIdReadFalse(Model model, HttpServletRequest request) throws JsonProcessingException {
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/",
                "Сообщения", "/messages/"
        );
        model.addAttribute("current_page", "messages");
        var token = getToken(request);
        List<InnerMessageDTO> innerMessages;
        if (token != null) {
            var userInfo = authService.userInfo(token);
            innerMessages = notifications.findBotMessageByUserId(token, userInfo.getId());
            model.addAttribute("innerMessages", innerMessages);
            model.addAttribute("userId", userInfo.getId());
            model.addAttribute("listIsEmpty", innerMessages.isEmpty());
            model.addAttribute("listIsNotEmpty", !innerMessages.isEmpty());
        }
        return "messages/messages";
    }
}
