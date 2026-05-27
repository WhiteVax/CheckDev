package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.site.domain.VacancyURL;
import ru.job4j.site.dto.*;
import ru.job4j.site.enums.StatusInterview;
import ru.job4j.site.service.*;
import ru.job4j.site.util.RequestResponseTools;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static ru.job4j.site.util.RequestResponseTools.getToken;

@Controller
@RequestMapping("/interview")
@AllArgsConstructor
@Slf4j
public class InterviewController {

    private final AuthService authService;
    private final TopicsService topicsService;
    private final InterviewService interviewService;
    private final InterviewsService interviewsService;
    private final WisherService wisherService;
    private final NotificationService notifications;
    private final FeedbackService feedbackService;
    private final ExamService examService;

    @GetMapping("/createForm")
    public String createForm(@ModelAttribute("topicId") int topicId,
                             Model model,
                             HttpServletRequest request)
            throws JsonProcessingException {
        var token = getToken(request);
        if (token != null) {
            var userInfo = authService.userInfo(token);
            var interviewsNoFeedback = interviewsService.findAllIdByNoFeedback(userInfo.getId());
            interviewsNoFeedback = interviewsNoFeedback.stream()
                    .filter(i -> i.getStatusId() != StatusInterview.IS_CANCELED.getId()).toList();
            model.addAttribute("noFeedback", interviewsNoFeedback);
            model.addAttribute("innerMessages", notifications.findBotMessageByUserId(token, userInfo.getId()));
        }
        var topic = topicsService.getById(topicId);
        var categoryName = topic.getCategory().getName();
        int categoryId = topic.getCategory().getId();
        Set<String> examSet = new HashSet<>();
        model.addAttribute("category", topic.getCategory());
        model.addAttribute("topic", topic);
        model.addAttribute("examSet", examSet);
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/index",
                "Категории", "/categories/",
                categoryName, String.format("/topics/%d", categoryId),
                topic.getName(), String.format("/topic/%d", topicId),
                "Создание собеседования", String.format("/interview/createForm?topicId=%d", topicId));
        return "interview/createForm";
    }

    @PostMapping("/create")
    public String createInterview(@ModelAttribute InterviewDTO interviewDTO,
                                  @ModelAttribute("topicId") int topicId,
                                  HttpServletRequest req)
            throws JsonProcessingException {
        var token = getToken(req);
        if (token != null) {
            var userInfo = authService.userInfo(token);
            interviewDTO.setSubmitterId(userInfo.getId());
            interviewDTO.setAuthor(userInfo.getUsername());
        }
        interviewDTO.setTopicId(topicId);
        interviewDTO.setContactBy(StringEscapeUtils.escapeHtml4(interviewDTO.getContactBy()));
        interviewDTO.setAdditional(StringEscapeUtils.escapeHtml4(interviewDTO.getAdditional()));
        interviewDTO.setTitle(StringEscapeUtils.escapeHtml4(interviewDTO.getTitle()));
        InterviewDTO createInterview = interviewService.create(getToken(req), interviewDTO);
        var categoryIdName = topicsService.getCategoryIdNameDTOByTopicId(topicId);
        var topicName = topicsService.getNameById(topicId);
        var categoryWithTopicDTO = new CategoryWithTopicDTO(
                categoryIdName.getId(), categoryIdName.getName(),
                topicId, topicName, createInterview.getId(), interviewDTO.getSubmitterId());
        notifications.notifyAboutInterviewCreation(token,
                categoryWithTopicDTO);
        var interviewNotifiDTO = InterviewNotifyDTO.of()
                .id(interviewDTO.getId())
                .submitterId(interviewDTO.getSubmitterId())
                .title(interviewDTO.getTitle())
                .topicId(interviewDTO.getTopicId())
                .topicName(topicName)
                .categoryId(categoryIdName.getId())
                .categoryName(categoryIdName.getName())
                .build();
        notifications.sendSubscribeTopic(token, interviewNotifiDTO);
        return "redirect:/interview/" + createInterview.getId();
    }

    @GetMapping("/{interviewId}")
    public String details(@PathVariable("interviewId") int interviewId,
                          Model model,
                          HttpServletRequest req) throws JsonProcessingException {
        var token = getToken(req);
        var interview = interviewService.getById(token, interviewId);
        var userInfo = authService.userInfo(token);
        var isAuthor = interviewService.isAuthor(userInfo, interview);
        var wishers = wisherService.getAllWisherDtoByInterviewId(token, String.valueOf(interview.getId()));
        var isWisher = wisherService.isWisher(userInfo.getId(), interview.getId(), wishers);
        var statisticMap = wisherService.getInterviewStatistic(wishers);
        var wishersDetail = interviewService.getAllWisherDetail(wishers);
        boolean isDismissed = wisherService.isDismissed(interviewId, wishers);
        var topicLiteDTO = topicsService.getTopicLiteDTOById(interview.getTopicId()).orElse(new TopicLiteDTO());
        topicLiteDTO.setText(StringEscapeUtils.unescapeHtml4(topicLiteDTO.getText()));
        boolean isUserDismissed = wisherService.isUserDismissed(interviewId, userInfo.getId(), wishers);
        var feedbacks = feedbackService.findByInterviewId(interview.getId());
        var feedbackMap = feedbackService.feedbackDTOSToMap(feedbacks);
        model.addAttribute("authService", authService);
        model.addAttribute("interview", interview);
        model.addAttribute("isAuthor", isAuthor);
        model.addAttribute("isWisher", isWisher);
        model.addAttribute("statisticMap", statisticMap);
        model.addAttribute("STATUS_IS_NEW_ID", StatusInterview.IS_NEW.getId());
        model.addAttribute("STATUS_IN_PROGRESS_ID", StatusInterview.IN_PROGRESS.getId());
        model.addAttribute("STATUS_IS_FEEDBACK_ID", StatusInterview.IS_FEEDBACK.getId());
        model.addAttribute("STATUS_IS_COMPLETED_ID", StatusInterview.IS_COMPLETED.getId());
        model.addAttribute("STATUS_IS_CANCELED_ID", StatusInterview.IS_CANCELED.getId());
        model.addAttribute("wishersDetail", wishersDetail);
        model.addAttribute("isDismissed", isDismissed);
        model.addAttribute("topicLiteDTO", topicLiteDTO);
        model.addAttribute("isUserDismissed", isUserDismissed);
        model.addAttribute("feedbackMap", feedbackMap);
        if (token != null) {
            model.addAttribute("innerMessages",
                    notifications.findBotMessageByUserId(token, userInfo.getId()));
        }
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/index",
                "Собеседования", "/interviews/",
                String.format("%s / %s", topicLiteDTO.getCategoryName(), topicLiteDTO.getName()),
                String.format("/interview/%d", interviewId));
        if (interview.getStatusId() == StatusInterview.IS_CANCELED.getId() && !isAuthor) {
            return "redirect:/interviews/";
        }
        return "interview/details";
    }

    /**
     * Метод пост отменяет интервью (собеседование), сохраняет текстовый комментарий с причиной его отмены
     * и отправляет уведомление об отмене откликнувшемуся участнику собеседования, если такой имелся.
     *
     * @param interviewDTO InterviewDTO
     * @param request      HttpServletRequest
     * @return String page.
     */
    @PostMapping("/cancel")
    public String cancelInterview(@ModelAttribute InterviewDTO interviewDTO,
                                  HttpServletRequest request) throws JsonProcessingException {
        var token = getToken(request);
        var interviewDTOfromDB = interviewService.getById(token, interviewDTO.getId());
        interviewDTOfromDB.setStatusId(StatusInterview.IS_CANCELED.getId());
        interviewDTOfromDB.setCancelBy(interviewDTO.getCancelBy());
        CompletableFuture.runAsync(() -> {
            try {
                interviewService.update(token, interviewDTOfromDB);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        List<WisherDto> wisherList = wisherService
                .getAllWisherDtoByInterviewId(token, String.valueOf(interviewDTOfromDB.getId()));
        if (wisherList.size() > 0) {
            wisherList.forEach(wisherDto -> {
                CancelInterviewNotificationDTO cancelInterviewDTO = new CancelInterviewNotificationDTO(
                        interviewDTOfromDB.getId(),
                        interviewDTOfromDB.getTitle(),
                        interviewDTOfromDB.getSubmitterId(),
                        interviewDTOfromDB.getAuthor(),
                        interviewDTOfromDB.getCancelBy(),
                        wisherDto.getUserId()
                );
                notifications.sendParticipateCancelInterview(token, cancelInterviewDTO);
            });
        }
        return "redirect:/interviews/";
    }

    /**
     * Метод отображает страницу с возможностью вставки ссылки на вакансию с целью генерации Собеседования и
     * вопросов на основе ключевых слов из вакансии.
     *
     * @return String view page
     */
    @GetMapping("/getVacancyLink")
    public String createInterviewByLink(HttpServletRequest request, Model model
    ) throws JsonProcessingException {
        var token = getToken(request);
        VacancyURL vacancyURL = new VacancyURL();
        model.addAttribute("vacancyURL", vacancyURL);
        return "interview/getVacancyLink";
    }

    /**
     * Метод пост отправляет ссылку на вакансию для генерации вопросов для собеседования.
     *
     * @param vacancyURL VacancyURL
     * @param request    HttpServletRequest
     * @return String view page
     */
    @PostMapping("/createInterviewByLink")
    public String createInterviewByLink(@ModelAttribute VacancyURL vacancyURL, Model model,
                                        HttpServletRequest request) throws Exception {
        var token = getToken(request);
        Set<String> examSet = new HashSet<>();
        if (token != null) {
            var userInfo = authService.userInfo(token);
            var interviewsNoFeedback = interviewsService.findAllIdByNoFeedback(userInfo.getId());
            interviewsNoFeedback = interviewsNoFeedback.stream()
                    .filter(i -> i.getStatusId() != StatusInterview.IS_CANCELED.getId()).toList();
            model.addAttribute("noFeedback", interviewsNoFeedback);
            model.addAttribute("innerMessages", notifications.findBotMessageByUserId(token, userInfo.getId()));
            examSet = examService.create(token, vacancyURL.getVacancyLink()).getQuestions();
        }
        var topicLiteDTO = topicsService.getAllTopicLiteDTO().stream()
                .filter(x -> x.getName().equals("Основанное на описании вакансии"))
                .toList()
                .get(0);
        var topic = topicsService.getById(topicLiteDTO.getId());
        var topicId = topic.getId();
        var categoryName = topic.getCategory().getName();
        int categoryId = topic.getCategory().getId();
        model.addAttribute("category", topic.getCategory());
        model.addAttribute("topic", topic);
        model.addAttribute("topicId", topic.getId());
        model.addAttribute("examSet", examSet);
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/index",
                "Категории", "/categories/",
                categoryName, String.format("/topics/%d", categoryId),
                topic.getName(), String.format("/topic/%d", topicId),
                "Создание собеседования", String.format("/interview/createForm?topicId=%d", topicId));
        return "interview/createForm";
    }

    /**
     * Метод отображает страницу с возможностью оставления комментария с причиной отмены интервью.
     * Для редактирования собеседования авторизованный пользователь должен быть создателем интервью(собеседования),
     * а само собеседование должно быть в статусе "Новое" или "В процессе"
     *
     * @param interviewId int
     * @param request     HttpServletRequest
     * @return String view page
     */
    @GetMapping("/cancel/reason/{interviewId}")
    public String reasonOfCancelInterview(@PathVariable("interviewId") int interviewId, Model model,
                                          HttpServletRequest request) throws JsonProcessingException {
        var token = getToken(request);
        var interviewDTO = new InterviewDTO();
        try {
            interviewDTO = interviewService.getById(token, interviewId);
        } catch (Exception e) {
            log.error("InterviewService.class method getById error: {}", e.getMessage());
            return "redirect:/";
        }
        var userInfo = authService.userInfo(token);
        var isAuthor = interviewService.isAuthor(userInfo, interviewDTO);
        if (interviewDTO.getStatusId() != StatusInterview.IS_NEW.getId()
                & interviewDTO.getStatusId() != StatusInterview.IN_PROGRESS.getId()) {
            return "redirect:/";
        }
        if (!isAuthor) {
            return "redirect:/";
        }
        model.addAttribute("interview", interviewDTO);
        return "interview/reasonOfCancel";
    }

    /**
     * Метод отображает страницу редактирования интервью (собеседования).
     * Для редактирования собеседования авторизованный пользователь должен быть создателем интервью(собеседования)
     *
     * @param interviewId int
     * @param model       Model
     * @param request     HttpServletRequest
     * @return String view page
     */
    @GetMapping("/edit/{id}")
    public String getEditView(@PathVariable("id") int interviewId,
                              Model model,
                              HttpServletRequest request) {
        var token = getToken(request);
        InterviewDTO interview;
        UserInfoDTO userInfoDTO;
        try {
            userInfoDTO = authService.userInfo(token);
            interview = interviewService.getById(token, interviewId);
            interview.setAdditional(StringEscapeUtils.unescapeHtml4(interview.getAdditional()));
            if (interview.getSubmitterId() != userInfoDTO.getId()) {
                return "redirect:/interview/" + interviewId;
            }
        } catch (Exception e) {
            log.error("Remote application not responding. Error: {}. {}, ", e.getCause(), e.getMessage());
            return "redirect:/interviews/";
        }
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/index",
                "Собеседования", "/interviews/",
                interview.getTitle(), String.format("/interview/edit/%d", interviewId));
        model.addAttribute("interview", interview);
        if (token != null) {
            model.addAttribute("innerMessages", notifications.findBotMessageByUserId(token,
                    userInfoDTO.getId()));
        }
        return "interview/interviewEdit";
    }

    /**
     * Метод обновления собеседования
     *
     * @param interview InterviewDTO.class
     * @param request   HttpServletRequest
     * @return String redirect page
     */
    @PostMapping("/update")
    public String postUpdateInterview(@ModelAttribute InterviewDTO interview,
                                      HttpServletRequest request,
                                      RedirectAttributes redirectAttributes) {
        var token = getToken(request);
        try {
            interview.setContactBy(StringEscapeUtils.escapeHtml4(interview.getContactBy()));
            interview.setAdditional(StringEscapeUtils.escapeHtml4(interview.getAdditional()));
            interview.setTitle(StringEscapeUtils.escapeHtml4(interview.getTitle()));
            interviewService.update(token, interview);
        } catch (Exception e) {
            log.error("Remote application not responding. Error, {}. {}, ", e.getCause(), e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Собеседование не обновлено");
            return "redirect:/interview/edit/" + interview.getId();
        }
        return "redirect:/interview/" + interview.getId();
    }

    @GetMapping("/{interviewId}/participate")
    public String participate(@PathVariable("interviewId") int interviewId,
                              Model model,
                              HttpServletRequest req) throws JsonProcessingException {
        var token = getToken(req);
        var userInfoDTO = authService.userInfo(token);
        var interview = interviewService.getById(token, interviewId);
        if (token != null) {
            model.addAttribute("botMessages",
                    notifications.findBotMessageByUserId(token, userInfoDTO.getId()));
        }
        var result = "interview/participate";
        if (userInfoDTO != null && interview.getSubmitterId() != userInfoDTO.getId()) {
            var wishers = wisherService
                    .getAllWisherDtoByInterviewId(token, String.valueOf(interview.getId()));
            var isWisher = wisherService.isWisher(userInfoDTO.getId(), interview.getId(), wishers);
            var statisticMap = wisherService.getInterviewStatistic(wishers);
            var countWishers = wisherService.countWishers(wishers, interviewId);
            model.addAttribute("interview", interview);
            model.addAttribute("isWisher", isWisher);
            model.addAttribute("statisticMap", statisticMap);
            model.addAttribute("countWishers", countWishers);
            RequestResponseTools.addAttrBreadcrumbs(model,
                    "Главная", "/index",
                    "Собеседования", "/interviews/",
                    interview.getTitle(), String.format("/interview/%d", interviewId),
                    "принять участие в собеседовании", "/participate");
        } else {
            result = String.format("redirect:/interview/%d", interviewId);
        }
        return result;
    }
}
