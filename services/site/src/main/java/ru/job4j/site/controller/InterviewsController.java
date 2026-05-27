package ru.job4j.site.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.site.component.FilterRequestParamsManager;
import ru.job4j.site.dto.FilterDTO;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.dto.ProfileDTO;
import ru.job4j.site.dto.TopicIdNameDTO;
import ru.job4j.site.enums.InterviewMode;
import ru.job4j.site.enums.StatusInterview;
import ru.job4j.site.service.*;
import ru.job4j.site.util.RequestResponseTools;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

import static ru.job4j.site.util.RequestResponseTools.getToken;

@Controller
@RequestMapping("/interviews")
@Slf4j
@AllArgsConstructor
public class InterviewsController {

    private final InterviewsService interviewsService;
    private final ProfilesService profilesService;
    private final CategoriesService categoriesService;
    private final TopicsService topicsService;
    private final AuthService authService;
    private final FilterService filterService;
    private final NotificationService notifications;
    private final FilterRequestParamsManager filterRequestParamsManager;

    @GetMapping("/")
    public String getAllInterviews(Model model,
                                   HttpServletRequest req,
                                   @RequestParam(required = false, defaultValue = "0") int page,
                                   @RequestParam(required = false, defaultValue = "20") int size,
                                   HttpSession session) {
        try {
            var token = getToken(req);
            var user = authService.userInfo(token);
            var userId = user != null ? user.getId() : 0;
            var filter = userId > 0
                    ? filterService.getByUserId(token, userId)
                    : (FilterDTO) session.getAttribute("filter");
            var isFiltered = filter != null
                    && (filter.getCategoryId() > 0 || filter.getFilterProfile() > 0
                    || filter.getStatus() > 0 || filter.getMode() > 0);
            Page<InterviewDTO> interviewsPage;
            List<TopicIdNameDTO> topicIdNameDTOS = new ArrayList<>();
            var categoryName = "";
            var topicName = "";
            var filterProfileName = "";
            var statusName = "";
            var modeName = "";
            var filterProfiles = filterService.getProfiles();
            var categories = categoriesService.getAll();
            if (isFiltered) {
                var categoryId = filter.getCategoryId();
                var topicId = filter.getTopicId();
                var filterProfileId = filter.getFilterProfile();
                var statusId = filter.getStatus();
                var modeId = filter.getMode();
                if (categoryId > 0) {
                    topicIdNameDTOS = topicsService.getTopicIdNameDtoByCategory(categoryId);
                }
                var filtersRequestParams = topicId > 0 ? filterRequestParamsManager
                        .getParams(filter) : filterRequestParamsManager.getParams(filter,
                        topicIdNameDTOS.stream().map(TopicIdNameDTO::getId).toList());
                interviewsPage = interviewsService.getAllWithFilters(filtersRequestParams, page, size);
                categoryName = categoriesService.getNameById(categories, categoryId);
                topicName = topicId > 0 ? topicsService.getNameById(topicId) : "";
                filterProfileName = filterProfileId > 0
                        ? filterService.getNameById(filterProfiles, filterProfileId) : "";
                statusName = statusId > 0 ? StatusInterview.values()[statusId].getInfo() : "";
                modeName = modeId > 0 ? InterviewMode.values()[modeId - 1].getInfo() : "";
            } else {
                interviewsPage = interviewsService.getAllByUserIdRelated(token, page, size, userId);
            }
            Set<ProfileDTO> userList = interviewsPage.toList().stream()
                    .map(x -> profilesService.getProfileById(x.getSubmitterId()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            interviewsService.setCountWishers(interviewsPage.toList(), token);
            RequestResponseTools.addAttrBreadcrumbs(model,
                    "Главная", "/index",
                    "Собеседования", String.format("/interviews/?page=%d&?size=%d", page, size)
            );
            var topicLiteDTOs = topicsService.getAllTopicLiteDTO();
            var topicsLiteMap = topicsService.liteDTTOSToMap(topicLiteDTOs);
            var statuses = StatusInterview.values();
            model.addAttribute("authService", authService);
            model.addAttribute("topicsLiteMap", topicsLiteMap);
            model.addAttribute("interviewsPage", interviewsPage);
            model.addAttribute("current_page", "interviews");
            model.addAttribute("users", userList);
            model.addAttribute("categories", categories);
            model.addAttribute("filter", filter);
            model.addAttribute("userId", userId);
            model.addAttribute("categoryName", categoryName);
            model.addAttribute("topicName", topicName);
            model.addAttribute("topics", topicIdNameDTOS);
            model.addAttribute("filterProfiles", filterProfiles);
            model.addAttribute("filterProfileName", filterProfileName);
            model.addAttribute("statuses", Arrays.copyOfRange(statuses, 1, statuses.length));
            model.addAttribute("modes", InterviewMode.values());
            model.addAttribute("innerMessages", notifications.findBotMessageByUserId(token, userId));
            model.addAttribute("statusName", statusName);
            model.addAttribute("modeName", modeName);
            model.addAttribute("STATUS_IS_CANCELED_ID", StatusInterview.IS_CANCELED.getId());
            if (token != null) {
                model.addAttribute("botMessages",
                        notifications.findBotMessageByUserId(token, user.getId()));
            }
        } catch (Exception e) {
            RequestResponseTools.addAttrBreadcrumbs(model,
                    "Главная", "/index",
                    "Собеседования", String.format("/interviews/?page=%d&?size=%d", page, size)
            );
            log.error("Remote application not responding. Error: {}. {}, ", e.getCause(), e.getMessage());
        }
        return "interview/interviews";
    }

    @PostMapping("/reload")
    @ResponseBody
    public void reload(@RequestBody FilterDTO filter,
                       Model model,
                       HttpServletRequest req,
                       @RequestParam(required = false, defaultValue = "0") int page,
                       @RequestParam(required = false, defaultValue = "20") int size,
                       HttpSession session) {
        session.setAttribute("filter", filter);
        getAllInterviews(model, req, page, size, session);
    }
}
