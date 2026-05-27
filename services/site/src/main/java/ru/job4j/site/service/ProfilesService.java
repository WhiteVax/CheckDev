package ru.job4j.site.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.job4j.site.dto.ProfileDTO;
import ru.job4j.site.dto.ProfileWithApprovedInterviewsDTO;
import ru.job4j.site.dto.UsersApprovedInterviewsDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CheckDev пробное собеседование
 * ProfileService класс обработки логики с моделью ProfileDTO
 *
 * @author Dmitry Stepanov
 * @version 23.09.2023T03:05
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProfilesService {
    private static final String URL_PROFILES = "/profiles/";
    private static final long ZERO_APPROVED_INTERVIEWS = 0;
    private final WebClientAuthCall webClientAuthCall;

    /**
     * Метод получает из сервиса Auth один профиль по ID
     *
     * @param id Int ID
     * @return Optional<ProfileDTO>
     */
    public Optional<ProfileDTO> getProfileById(int id) {
        ResponseEntity<ProfileDTO> profile = webClientAuthCall
                .doGetReqParam(URL_PROFILES + id)
                .block();
        return Optional.ofNullable(profile.getBody());
    }

    /**
     * Метод получает из сервиса Auth список всех профилей.
     *
     * @return List<ProfileDTO>
     */
    private List<ProfileDTO> getAllProfile() {
        var responseEntity = webClientAuthCall
                .doGetReqParamAll(URL_PROFILES)
                .block();
        return responseEntity.getBody();
    }

    /**
     * Возвращаем все профили с информацией о количестве проведенных собеседованиях.
     * Сортировка списка идет по количеству проеденных собеседований от большего к меньшему.
     *
     * @param users List<UsersApprovedInterviewsDTO>
     * @return List<ProfileWithApprovedInterviewsDTO>
     */
    public List<ProfileWithApprovedInterviewsDTO> getAllProfilesWithApprovedInterviews(List<UsersApprovedInterviewsDTO> users) {
        var profiles = getAllProfile();
        List<ProfileWithApprovedInterviewsDTO> profilesWithCountOfInterviews = new ArrayList<>();
        for (ProfileDTO profile : profiles) {
            ProfileWithApprovedInterviewsDTO profileDto = new ProfileWithApprovedInterviewsDTO(profile, ZERO_APPROVED_INTERVIEWS);
            for (UsersApprovedInterviewsDTO user : users) {
                if (profile.getId() == user.getUserId()) {
                    profileDto.setApprovedInterviews(user.getApprovedInterviews());
                }
            }
            profilesWithCountOfInterviews.add(profileDto);
        }
        profilesWithCountOfInterviews.sort(
                (a, b) -> Long.compare(b.getApprovedInterviews(), a.getApprovedInterviews()));
        return profilesWithCountOfInterviews;
    }
}
