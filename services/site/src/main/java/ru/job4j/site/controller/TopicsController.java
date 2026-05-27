package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.site.service.AuthService;
import ru.job4j.site.service.CategoriesService;
import ru.job4j.site.service.NotificationService;
import ru.job4j.site.service.TopicsService;
import ru.job4j.site.util.RequestResponseTools;

import javax.servlet.http.HttpServletRequest;

import static ru.job4j.site.util.RequestResponseTools.getToken;

@Controller
@RequestMapping("/topics")
@AllArgsConstructor
@Slf4j
public class TopicsController {
    private final CategoriesService categoriesService;
    private final TopicsService topicsService;
    private final AuthService authService;

    private final NotificationService notifications;

    @GetMapping("/{categoryId}")
    public String getByCategory(@PathVariable int categoryId,
                                Model model,
                                HttpServletRequest req) throws JsonProcessingException {
        var category = categoriesService.getById(categoryId);
        if (category.isEmpty()) {
            return "redirect:/categories/";
        }
        var topics = topicsService.getTopicsWithCountInterview(categoryId);
        model.addAttribute("category", category.get());
        model.addAttribute("topics", topics);
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/index",
                "Категории", "/categories/",
                category.get().getName(), String.format("/topics/%d", category.get().getId()));
        try {
            var token = getToken(req);
            if (token != null) {
                var userInfo = authService.userInfo(token);
                model.addAttribute("userInfo", userInfo);
                RequestResponseTools.addAttrCanManage(model, userInfo);
                model.addAttribute("userTopicDTO", notifications.findTopicByUserId(userInfo.getId()).orElse(null));
                model.addAttribute("innerMessages", notifications.findBotMessageByUserId(token, userInfo.getId()));
            }
        } catch (Exception e) {
            RequestResponseTools.addAttrBreadcrumbs(model,
                    "Главная", "/index",
                    "Категории", "/categories/",
                    category.get().getName(), String.format("/topics/%d", category.get().getId()));
            log.error("Remote application not responding. Error: {}. {}, ", e.getCause(), e.getMessage());
        }
        return "topic/topics";
    }
}