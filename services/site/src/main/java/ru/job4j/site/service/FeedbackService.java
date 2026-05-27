package ru.job4j.site.service;

import ru.job4j.site.dto.FeedbackDTO;

import java.util.List;
import java.util.Map;

/**
 * FeedbackService интерфейс описывает поведение обработки сущности Feedback
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 25.10.2023
 */
public interface FeedbackService {
    boolean save(String token, FeedbackDTO feedbackDTO, String name);

    List<FeedbackDTO> findByInterviewId(int interviewId);

    List<FeedbackDTO> findByInterviewIdAndUserId(int interviewId, int userID);

    Map<Integer, List<FeedbackDTO>> feedbackDTOSToMap(List<FeedbackDTO> feedbackDTOS);
}
