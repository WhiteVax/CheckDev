package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.site.dto.TopicDTO;
import ru.job4j.site.dto.TopicLiteDTO;
import ru.job4j.site.service.AuthService;
import ru.job4j.site.service.NotificationService;
import ru.job4j.site.service.TopicsService;
import ru.job4j.site.util.RequestResponseTools;

import javax.servlet.http.HttpServletRequest;

import static ru.job4j.site.util.RequestResponseTools.getToken;

@Controller
@RequestMapping("/topic")
@AllArgsConstructor
@Slf4j
public class TopicController {
    private final TopicsService topicsService;
    private final AuthService authService;
    private final NotificationService notifications;

    @GetMapping("/{topicId}")
    public String details(@PathVariable int topicId,
                          Model model,
                          HttpServletRequest req) throws JsonProcessingException {
        var topic = topicsService.getById(topicId);
        topic.setName(StringEscapeUtils.unescapeHtml4(topic.getName()));
        topic.setText(StringEscapeUtils.unescapeHtml4(topic.getText()));
        String categoryName = topic.getCategory().getName();
        int categoryId = topic.getCategory().getId();
        model.addAttribute("topic", topic);
        try {
            var token = getToken(req);
            if (token != null) {
                var userInfo = authService.userInfo(token);
                model.addAttribute("userTopicDTO", notifications.findTopicByUserId(userInfo.getId()).orElse(null));
                model.addAttribute("innerMessages", notifications.findBotMessageByUserId(token, userInfo.getId()));
                RequestResponseTools.addAttrCanManage(model, userInfo);
            }
            RequestResponseTools.addAttrBreadcrumbs(model,
                    "Главная", "/index",
                    "Категории", "/categories/",
                    categoryName, String.format("/topics/%d", categoryId),
                    topic.getName(), String.format("/topic/%d", topicId));
        } catch (Exception e) {
            RequestResponseTools.addAttrBreadcrumbs(model,
                    "Главная", "/index",
                    "Категории", "/categories/",
                    categoryName, String.format("/topics/%d", categoryId));
            log.error("Remote application not responding. Error: {}. {}, ", e.getCause(), e.getMessage());
        }
        return "topic/details";
    }

    @GetMapping("/createForm/{categoryId}")
    public String createForm(@PathVariable int categoryId, Model model, HttpServletRequest req)
            throws JsonProcessingException {
        model.addAttribute("categoryId", categoryId);
        var token = getToken(req);
        if (token != null) {
            var userInfo = authService.userInfo(token);
            model.addAttribute("innerMessages", notifications.findBotMessageByUserId(token, userInfo.getId()));
        }
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/index",
                "Категории", "/categories/",
                "Темы", String.format("/topics/%d", categoryId),
                "Создание темы", String.format("/topic/createForm/%d", categoryId));
        return "topic/createForm";
    }

    @PostMapping("/create")
    public String createTopic(@ModelAttribute TopicLiteDTO topic, HttpServletRequest req,
                              RedirectAttributes redirectAttributes)
            throws JsonProcessingException {
        topic.setName(StringEscapeUtils.escapeHtml4(topic.getName()));
        topic.setText(StringEscapeUtils.escapeHtml4(topic.getText()));
        topic.setCategoryName(StringEscapeUtils.escapeHtml4(topic.getCategoryName()));
        var createdTopic = topicsService.create(getToken(req), topic);
        var notNull = createdTopic != null;
        if (notNull) {
            redirectAttributes.addAttribute("topicId", createdTopic.getId());
        }
        return notNull ? "redirect:/topic/{topicId}" : "redirect:/categories/";
    }

    @GetMapping("/updateForm/{topicId}")
    public String updateForm(Model model,
                             HttpServletRequest req,
                             @PathVariable int topicId)
            throws JsonProcessingException {
        var topic = new TopicDTO();
        topic.setName("");
        var token = getToken(req);
        if (token != null) {
            var userInfo = authService.userInfo(token);
            topic = topicsService.getById(topicId);
            topic.setName(StringEscapeUtils.unescapeHtml4(topic.getName()));
            topic.setText(StringEscapeUtils.unescapeHtml4(topic.getText()));
            RequestResponseTools.addAttrCanManage(model, userInfo);
            model.addAttribute("userInfo", userInfo);
            model.addAttribute("innerMessages", notifications.findBotMessageByUserId(token, userInfo.getId()));
        }
        model.addAttribute("topic", topic);
        int categoryId = topic.getCategory().getId();
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/index",
                "Категории", "/categories/",
                topic.getCategory().getName(), String.format("/topics/%d", categoryId),
                "Редактировать тему", String.format("/topic/updateForm/%d", topicId));
        return "topic/updateForm";
    }

    @PostMapping("/update")
    public String updateTopic(TopicDTO topic, HttpServletRequest req) throws JsonProcessingException {
        topic.setName(StringEscapeUtils.escapeHtml4(topic.getName()));
        topic.setText(StringEscapeUtils.escapeHtml4(topic.getText()));
        topicsService.update(getToken(req), topic);
        return "redirect:/topic/" + topic.getId();
    }

    @PostMapping("/delete")
    public String deleteTopic(@ModelAttribute(name = "id") int id, HttpServletRequest req)
            throws JsonProcessingException {
        topicsService.delete(getToken(req), id);
        return "redirect:/categories/";
    }
}
