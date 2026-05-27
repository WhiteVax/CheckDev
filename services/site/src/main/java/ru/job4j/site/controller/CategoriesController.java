package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.site.service.AuthService;
import ru.job4j.site.service.CategoriesService;
import ru.job4j.site.service.NotificationService;
import ru.job4j.site.util.RequestResponseTools;

import javax.servlet.http.HttpServletRequest;

import static ru.job4j.site.util.RequestResponseTools.getToken;

@Controller
@RequestMapping("/categories")
@AllArgsConstructor
@Slf4j
public class CategoriesController {
    private final CategoriesService categoriesService;
    private final AuthService authService;
    private final NotificationService notifications;

    @GetMapping("/")
    public String categories(Model model, HttpServletRequest req) throws JsonProcessingException {
        try {
            var categories = categoriesService.getAllWithTopics();
            categories.forEach(c -> c.setName(StringEscapeUtils.unescapeHtml4(c.getName())));
            model.addAttribute("categories", categories);
            var token = getToken(req);
            if (token != null) {
                var userInfo = authService.userInfo(token);
                model.addAttribute("userInfo", userInfo);
                model.addAttribute("userDTO", notifications.findCategoriesByUserId(userInfo.getId()).orElse(null));
                model.addAttribute("innerMessages", notifications.findBotMessageByUserId(token, userInfo.getId()));
                RequestResponseTools.addAttrCanManage(model, userInfo);
            }
            RequestResponseTools.addAttrBreadcrumbs(model,
                    "Главная", "/index",
                    "Категории", "/categories/"
            );
            model.addAttribute("current_page", "categories");
        } catch (Exception e) {
            RequestResponseTools.addAttrBreadcrumbs(model,
                    "Главная", "/index"
            );
            log.error("Remote application not responding. Error: {}. {}, ", e.getCause(), e.getMessage());
        }
        return "categories/categories";
    }
}
