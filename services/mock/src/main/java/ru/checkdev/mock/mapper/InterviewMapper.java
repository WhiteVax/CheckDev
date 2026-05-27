package ru.checkdev.mock.mapper;

import ru.checkdev.mock.domain.Interview;
import ru.checkdev.mock.dto.InterviewDTO;
import ru.checkdev.mock.enums.StatusInterview;
import ru.checkdev.mock.enums.StatusInterviewConverter;

/**
 * CheckDev пробное собеседование
 * Преобразователь Interview DTO<->DAO
 *
 * @author Dmitry Stepanov
 * @version 20.11.2023 22:03
 */
public class InterviewMapper {
    private static final StatusInterviewConverter CONVERTER = new StatusInterviewConverter();

    public static Interview getInterview(InterviewDTO interviewDTO) {
        StatusInterview statusInterview = CONVERTER.convertToEntityAttribute(interviewDTO.getStatusId());
        return Interview.of()
                .id(interviewDTO.getId())
                .mode(interviewDTO.getMode())
                .status(statusInterview)
                .submitterId(interviewDTO.getSubmitterId())
                .title(interviewDTO.getTitle())
                .additional(interviewDTO.getAdditional())
                .contactBy(interviewDTO.getContactBy())
                .approximateDate(interviewDTO.getApproximateDate())
                .topicId(interviewDTO.getTopicId())
                .author(interviewDTO.getAuthor())
                .agreedWisherId(interviewDTO.getAgreedWisherId())
                .cancelBy(interviewDTO.getCancelBy())
                .build();
    }

    public static InterviewDTO getInterviewDTO(Interview interview) {
        int statusID = getStatusIdByStatusInterview(interview.getStatus());
        StatusInterview statusInterview = getStatusInterviewById(statusID);
        return InterviewDTO.of()
                .id(interview.getId())
                .mode(interview.getMode())
                .statusId(statusInterview.getId())
                .statusInfo(statusInterview.getInfo())
                .submitterId(interview.getSubmitterId())
                .agreedWisherId(interview.getAgreedWisherId())
                .title(interview.getTitle())
                .additional(interview.getAdditional())
                .contactBy(interview.getContactBy())
                .approximateDate(interview.getApproximateDate())
                .createDate(interview.getCreateDate().toString())
                .topicId(interview.getTopicId())
                .author(interview.getAuthor())
                .cancelBy(interview.getCancelBy())
                .build();
    }

    public static StatusInterview getStatusInterviewById(int statusId) {
        return CONVERTER.convertToEntityAttribute(statusId);
    }

    public static Integer getStatusIdByStatusInterview(StatusInterview statusInterview) {
        return CONVERTER.convertToDatabaseColumn(statusInterview);
    }
}
