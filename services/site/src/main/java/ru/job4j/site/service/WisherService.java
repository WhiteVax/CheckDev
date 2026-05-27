package ru.job4j.site.service;

import ru.job4j.site.dto.InterviewStatistic;
import ru.job4j.site.dto.UsersApprovedInterviewsDTO;
import ru.job4j.site.dto.WisherDto;

import java.util.List;
import java.util.Map;

/**
 * Интерфейс WisherService
 * описывает поведения работы с сервисом mock, моделью Wisher.
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 11.10.2023
 */
public interface WisherService {

    boolean saveWisherDto(String token, WisherDto wisherDto);

    List<WisherDto> getAllWisherDtoByInterviewId(String token, String interviewId);

    boolean setNewApproveByWisherInterview(String token, String interviewId,
                                           String wisherId, boolean newApprove);

    boolean isWisher(int userId, int interviewId, List<WisherDto> wishers);

    boolean isDismissed(int interviewId, List<WisherDto> wishers);

    boolean isUserDismissed(int interviewId, int userId, List<WisherDto> wishers);

    Map<Integer, InterviewStatistic> getInterviewStatistic(List<WisherDto> wishers);

    Long countWishers(List<WisherDto> wishers, int interviewId);

    List<UsersApprovedInterviewsDTO> getUsersIdWithCountedApprovedInterviews(String token);

    UsersApprovedInterviewsDTO getUserIdWithCountedApprovedInterviews(String token, String userId);
}