package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.job4j.site.dto.*;
import ru.job4j.site.enums.StatusInterview;
import ru.job4j.site.service.InterviewService;
import ru.job4j.site.service.NotificationService;
import ru.job4j.site.service.WisherService;
import ru.job4j.site.util.RequestResponseTools;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Контроллер обработки запросов на подписку на собеседование.
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.10.2023
 */
@Controller
@RequestMapping("/wisher")
@RequiredArgsConstructor
public class WisherController {
    private final WisherService wisherService;
    private final InterviewService interviewService;
    private final NotificationService notificationService;

    @Value("${service.urlSite}")
    private String url;

    /**
     * Подать заявку на участие в собеседовании.
     *
     * @param wisherNotifyDTO WisherNotifyDTO
     * @param request         HttpServletRequest
     * @return String page.
     */
    @PostMapping("/create")
    public String createWisher(@ModelAttribute WisherNotifyDTO wisherNotifyDTO,
                               HttpServletRequest request) {
        var token = RequestResponseTools.getToken(request);
        int interviewId = wisherNotifyDTO.getInterviewId();
        int userId = wisherNotifyDTO.getUserId();
        wisherNotifyDTO.setContactBy(StringEscapeUtils.escapeHtml4(wisherNotifyDTO.getContactBy()));
        var contactBy = wisherNotifyDTO.getContactBy();
        var wisherDto = new WisherDto(0, interviewId, userId, contactBy, false);
        wisherService.saveWisherDto(token, wisherDto);
        notificationService.sendParticipateAuthor(token, wisherNotifyDTO);
        return "redirect:/interview/" + interviewId;
    }

    /**
     * Одобрить участника собеседование, а остальных участников отклонить
     *
     * @param param   Map<String, String>
     * @param request HttpServletRequest
     * @return String page.
     */
    @PostMapping("/dismissed")
    public String dismissedWisher(@RequestParam Map<String, String> param,
                                  @ModelAttribute WisherApprovedDTO wisherApprovedDTO,
                                  HttpServletRequest request) throws JsonProcessingException {
        var token = RequestResponseTools.getToken(request);
        var interviewId = param.get("interviewId");
        var wisherId = param.get("wisherId");
        var wisherUserId = param.get("wisherUserId");
        wisherService.setNewApproveByWisherInterview(
                token, interviewId, wisherId, true);
        InterviewDTO interviewDto = interviewService.getById(token, Integer.parseInt(interviewId));
        interviewDto.setAgreedWisherId(Integer.parseInt(wisherUserId));
        interviewDto.setStatusId(StatusInterview.IN_PROGRESS.getId());
        interviewService.update(token, interviewDto);
        interviewService.updateStatus(token, interviewDto);
        wisherApprovedDTO.setInterviewLink(
                String.format("%sinterview/%d", url, Integer.parseInt(interviewId)));
        CompletableFuture.runAsync(() -> notificationService.approvedWisher(token, wisherApprovedDTO));
        List<WisherDto> wisherDtoList = wisherService.getAllWisherDtoByInterviewId(token, interviewId)
                .stream().filter(x -> x.getId() != wisherApprovedDTO.getWisherId()).toList();
        if (!wisherDtoList.isEmpty()) {
            List<WisherDismissedDTO> wisherDismissedDTOList = new ArrayList<>();
            wisherDtoList.parallelStream().forEach(w -> {
                WisherDismissedDTO wisherDismissedDTO = new WisherDismissedDTO(
                        interviewDto.getId(),
                        interviewDto.getTitle(),
                        interviewDto.getSubmitterId(),
                        interviewDto.getAuthor(),
                        w.getUserId());
                wisherDismissedDTOList.add(wisherDismissedDTO);
            });
            notificationService.sendParticipantIsDismissed(token, wisherDismissedDTOList);
        }
        return "redirect:/interview/" + interviewId;
    }
}
