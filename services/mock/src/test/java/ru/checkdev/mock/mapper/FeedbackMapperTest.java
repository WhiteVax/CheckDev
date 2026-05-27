package ru.checkdev.mock.mapper;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.checkdev.mock.domain.Feedback;
import ru.checkdev.mock.domain.Interview;
import ru.checkdev.mock.dto.FeedbackDTO;
import ru.checkdev.mock.enums.StatusInterview;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FeedbackMapper test
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 25.10.2023
 */
@Disabled
class FeedbackMapperTest {

    @Test
    void whenMapperGetFeedbackDTOThenEquals() {
        var interview = new Interview(1, 1, StatusInterview.IS_NEW, 1,
                "titel", "add", "contact",
                "approxim", null, 1, "author", 1, "");
        var feedback = new Feedback(1, interview, 2, 1, "text", 5);
        var expected = new FeedbackDTO(feedback.getId(), feedback.getInterview().getId(), feedback.getUserId(),
                feedback.getRoleInInterview(), feedback.getTextFeedback(), feedback.getScope());
        var actual = FeedbackMapper.getFeedbackDTO(feedback);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void whenMapperGetFeedbackThenEquals() {
        var interview = new Interview(1, 1, StatusInterview.IS_NEW, 1,
                "titel", "add", "contact",
                "approxim", null, 1, "author", 1, "");
        var feedbackDTO = new FeedbackDTO(1, interview.getId(), 1,
                2, "text", 5);
        var actual = new Feedback(feedbackDTO.getId(), interview, feedbackDTO.getUserId(),
                feedbackDTO.getRoleInInterview(), feedbackDTO.getTextFeedback(), feedbackDTO.getScope());
        var expected = FeedbackMapper.getFeedback(feedbackDTO, interview);
        assertThat(actual).isEqualTo(expected);
    }
}