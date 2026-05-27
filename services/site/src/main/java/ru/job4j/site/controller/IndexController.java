package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.site.dto.CategoryDTO;
import ru.job4j.site.dto.VacancyStatisticWithDates;
import ru.job4j.site.service.*;
import ru.job4j.site.util.RequestResponseTools;

import javax.servlet.http.HttpServletRequest;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.job4j.site.util.RequestResponseTools.getToken;

@Controller
@AllArgsConstructor
@Slf4j
public class IndexController {
    private final CategoriesService categoriesService;
    private final InterviewsService interviewsService;
    private final AuthService authService;
    private final NotificationService notifications;
    private final TopicsService topicsService;
    private final VacancyStatisticService vacancyStatisticService;

    @GetMapping({"/", "index"})
    public String getIndexPage(Model model, HttpServletRequest req) throws JsonProcessingException {
        setMain(model, req, vacancyStatisticService.getAll());
        return "index";
    }

    @GetMapping({"/indexRenew"})
    public String getIndexPageWithLatestStatistic(Model model, HttpServletRequest req)
            throws JsonProcessingException {
        setMain(model, req, vacancyStatisticService.renew());
        return "index";
    }

    private void setMain(Model model, HttpServletRequest req,
                         VacancyStatisticWithDates vacancyStatistic)
            throws JsonProcessingException {
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/"
        );
        try {
            List<CategoryDTO> mostPopularCategories = categoriesService.getMostPopular();
            mostPopularCategories.forEach(c ->
                    c.setName(StringEscapeUtils.unescapeHtml4(c.getName())));
            model.addAttribute("categories", mostPopularCategories);
            var token = getToken(req);
            if (token != null) {
                var userInfo = authService.userInfo(token);
                model.addAttribute("userInfo", userInfo);
                model.addAttribute("userDTO",
                        notifications.findCategoriesByUserId(userInfo.getId()).orElse(null));
                model.addAttribute("innerMessages",
                        notifications.findBotMessageByUserId(token, userInfo.getId()));
                RequestResponseTools.addAttrCanManage(model, userInfo);
            }
        } catch (Exception e) {
            log.error("Remote application not responding. Error: {}. {}, ",
                    e.getCause(), e.getMessage());
        }
        var topicLiteDTOs = topicsService.getAllTopicLiteDTO();
        var topicsLiteMap = topicsService.liteDTTOSToMap(topicLiteDTOs);
        var newInterviewsDTO = interviewsService.getLast();
        newInterviewsDTO.forEach(i -> {
            i.setTitle(StringEscapeUtils.unescapeHtml4(i.getTitle()));
            i.setAdditional(StringEscapeUtils.unescapeHtml4(i.getAdditional()));
        });
        interviewsService.setCountWishers(newInterviewsDTO, getToken(req));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        var vacancyStatisticDates = vacancyStatistic.getDates();
        var vacancyStatisticLastUpdateDate =
                vacancyStatisticDates.getLastUpdate().format(formatter);
        var vacancyStatisticNextUpdateDate =
                vacancyStatisticDates.getNextUpdate().format(formatter);

        model.addAttribute("topicsLiteMap", topicsLiteMap);
        model.addAttribute("new_interviews", newInterviewsDTO);
        model.addAttribute("authService", authService);
        model.addAttribute("vacancyStatistic", vacancyStatistic.getStatisticList());
        model.addAttribute("vacancyStatisticLastUpdateDate", vacancyStatisticLastUpdateDate);
        model.addAttribute("vacancyStatisticNextUpdateDate", vacancyStatisticNextUpdateDate);
    }
}