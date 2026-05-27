package ru.job4j.site.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.site.dto.FeedbackDTO;
import ru.job4j.site.dto.FeedbackNotificationDTO;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.dto.ProfileDTO;
import ru.job4j.site.service.FeedbackService;
import ru.job4j.site.service.InterviewService;
import ru.job4j.site.service.NotificationService;
import ru.job4j.site.service.ProfilesService;
import ru.job4j.site.util.RequestResponseTools;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * FeedbackController контроллер обработки отзывов
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 25.10.2023
 */
@Controller
@RequestMapping("/interview")
@AllArgsConstructor
@Slf4j
public class FeedbackController {

    private FeedbackService feedbackService;
    private InterviewService interviewService;
    private ProfilesService profilesService;

    private NotificationService notificationService;

    /**
     * Отображение формы для сохранения отзыв на собеседование.
     *
     * @param id int ID Interview
     * @return String FeedbackForm page
     */
    @GetMapping("/feedback/{id}")
    public String getFeedbackForm(@PathVariable int id,
                                  Model model,
                                  HttpServletRequest request) {
        var token = RequestResponseTools.getToken(request);
        var interviewDTO = new InterviewDTO();
        try {
            interviewDTO = interviewService.getById(token, id);
        } catch (Exception e) {
            log.error("InterviewService.class method getById error: {}", e.getMessage());
            return "redirect:/";
        }
        ProfileDTO wisherProfileDTO = new ProfileDTO();
        Optional<ProfileDTO> wisherProfile = profilesService.getProfileById(interviewDTO.getAgreedWisherId());
        if (wisherProfile.isPresent()) {
            wisherProfileDTO = wisherProfile.get();
        }
        ProfileDTO authorProfileDTO = new ProfileDTO();
        Optional<ProfileDTO> authorProfile = profilesService.getProfileById(interviewDTO.getSubmitterId());
        if (authorProfile.isPresent()) {
            authorProfileDTO = authorProfile.get();
        }
        var feedbacks = feedbackService.findByInterviewId(interviewDTO.getId());
        var feedbackMap = feedbackService.feedbackDTOSToMap(feedbacks);
        model.addAttribute("feedbackMap", feedbackMap);
        model.addAttribute("interview", interviewDTO);
        model.addAttribute("agreedWisher", wisherProfileDTO);
        model.addAttribute("interviewAuthor", authorProfileDTO);
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/index",
                "Собеседования", "/interviews/",
                interviewDTO.getTitle(), String.format("/interview/%d", id),
                "Отзыв", String.format("/interview/feedback/%d", id));
        return "interview/feedbackForm";
    }

    /**
     * Метод пост сохраняет новый комментарий.
     *
     * @param feedbackDTO FeedbackDTO
     * @param request     HttpServletRequest
     * @return String page.
     */
    @PostMapping("/createFeedback")
    public String saveFeedback(@ModelAttribute FeedbackDTO feedbackDTO,
                               @ModelAttribute("userName") String name,
                               @ModelAttribute("userId") int userId,
                               @ModelAttribute("submitterId") int submitterId,
                               @ModelAttribute("agreedWisherId") int agreedWisherId,
                               @ModelAttribute("interviewTitle") String interviewTitle,
                               HttpServletRequest request) {
        var feedbacks = feedbackService.findByInterviewId(feedbackDTO.getInterviewId());
        for (FeedbackDTO f : feedbacks) {
            if (userId == f.getUserId()) {
                return "redirect:/interview/feedback/" + feedbackDTO.getInterviewId();
            }
        }
        var token = RequestResponseTools.getToken(request);
        feedbackDTO.setTextFeedback(StringEscapeUtils.escapeHtml4(feedbackDTO.getTextFeedback()));
        feedbackService.save(token, feedbackDTO, name);
        var recipientId = submitterId == userId ? agreedWisherId : submitterId;
        if (userId > 0 && recipientId > 0) {
            notificationService.sendFeedbackNotification(token,
                    new FeedbackNotificationDTO(recipientId, name, interviewTitle, feedbackDTO.getInterviewId()));
        }
        return "redirect:/interview/" + feedbackDTO.getInterviewId();
    }
}
