package ru.job4j.site.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import ru.job4j.site.dto.InterviewStatistic;
import ru.job4j.site.dto.UsersApprovedInterviewsDTO;
import ru.job4j.site.dto.WisherDto;

import java.util.*;

/**
 * WisherServiceWebClient
 * Класс реализует получение и обработку модели WisherDTO из сервиса mock
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 11.10.2023
 */
@Service
@Slf4j
public class WisherServiceWebClient implements WisherService {

    private WebClient webClientWisher;
    private static final String SERVICE_ID = "mock";
    private static final String DIRECT_SINGLE = "/wisher/";
    private static final String DIRECT_MULTIPLE = "/wishers/";

    public WisherServiceWebClient(EurekaUriProvider uriProvider) {
        this.webClientWisher = WebClient.create(uriProvider.getUri(SERVICE_ID));
    }

    /**
     * Метод сохраняет участника WisherDTO
     *
     * @param token     String
     * @param wisherDto WisherDto.class
     * @return boolean true/false
     */
    @Override
    public boolean saveWisherDto(String token, WisherDto wisherDto) {
        var responseEntityMono = this.webClientWisher
                .post()
                .uri(DIRECT_SINGLE)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(wisherDto)
                .retrieve()
                .toEntity(WisherDto.class)
                .doOnError(err -> log.error("API MOCK not found: {}", err.getMessage()))
                .blockOptional();
        return responseEntityMono
                .map(re -> re.getStatusCode().is2xxSuccessful())
                .orElse(false);
    }

    /**
     * Метод возвращает всех участников собеседования interviewId
     * Если interviewId == "" вернется список всех участников по всем собеседованиям.
     *
     * @param token       String
     * @param interviewId String
     * @return List<WisherDto>
     */
    @Override
    public List<WisherDto> getAllWisherDtoByInterviewId(String token, String interviewId) {
        Optional<ResponseEntity<List<WisherDto>>> listResponseEntity = this.webClientWisher
                .get()
                .uri(DIRECT_MULTIPLE + "dto/" + interviewId)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntityList(WisherDto.class)
                .doOnError(err -> log.error("API MOCK not found: {}", err.getMessage()))
                .blockOptional();
        return listResponseEntity
                .map(HttpEntity::getBody)
                .orElse(new ArrayList<>());
    }

    /**
     * Метод получает страницу со списком id пользователей с количеством проведенных ими интервью.
     *
     * @return List<WisherDTO>
     */
    @Override
    public List<UsersApprovedInterviewsDTO> getUsersIdWithCountedApprovedInterviews(String token) {
        Optional<ResponseEntity<List<UsersApprovedInterviewsDTO>>> listResponseEntity = this.webClientWisher
                .get()
                .uri(DIRECT_MULTIPLE + "approved/")
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntityList(UsersApprovedInterviewsDTO.class)
                .doOnError(err -> log.error("API MOCK not found: {}", err.getMessage()))
                .blockOptional();
        return listResponseEntity
                .map(HttpEntity::getBody)
                .orElse(new ArrayList<>());
    }

    /**
     * Метод возвращает пользователя с количеством проведенных им интервью.
     *
     * @param userId ID user ID
     * @return UsersApprovedInterviewsDTO
     */
    @Override
    public UsersApprovedInterviewsDTO getUserIdWithCountedApprovedInterviews(String token, String userId) {
        Optional<ResponseEntity<UsersApprovedInterviewsDTO>> responseEntity = this.webClientWisher
                .get()
                .uri(DIRECT_MULTIPLE + "approved/" + userId)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(UsersApprovedInterviewsDTO.class)
                .doOnError(err -> log.error("API MOCK not found: {}", err.getMessage()))
                .blockOptional();
        return responseEntity
                .map(HttpEntity::getBody)
                .orElse(new UsersApprovedInterviewsDTO());
    }

    /**
     * Метод по устанавливает новый статус участниках интервью
     *
     * @param token       User token
     * @param interviewId ID Interview
     * @param wisherId    ID select Wisher
     * @param newApprove  new Status ID select Wisher
     * @return boolean true / false
     */
    @Override
    public boolean setNewApproveByWisherInterview(String token, String interviewId,
                                                  String wisherId, boolean newApprove) {
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("interviewId", interviewId);
        param.add("wisherId", wisherId);
        param.add("newApprove", String.valueOf(newApprove));
        var setNewStatus = this.webClientWisher
                .post()
                .uri(DIRECT_MULTIPLE + "approve/")
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(param)
                .retrieve()
                .toEntity(HttpStatus.class)
                .doOnError(err -> log.error("API MOCK not found: {}", err.getMessage()))
                .blockOptional();
        return setNewStatus
                .map(re -> re.getStatusCode().is2xxSuccessful())
                .orElse(false);
    }

    /**
     * Метод проверяет, является пользователь участником конкретного интервью
     *
     * @param userId      User ID
     * @param interviewId Interview ID
     * @param wishers     List<WisherDto>
     * @return boolean true/false
     */
    @Override
    public boolean isWisher(int userId, int interviewId, List<WisherDto> wishers) {
        return wishers.stream()
                .anyMatch(wiser ->
                        wiser.getUserId() == userId
                                && wiser.getInterviewId() == interviewId);
    }

    /**
     * Метод проверяет одобрен хотя бы один участник собеседования.
     *
     * @param interviewId Interview ID
     * @param wishers     List<WisherDto>
     * @return boolean true or false
     */
    @Override
    public boolean isDismissed(int interviewId, List<WisherDto> wishers) {
        return wishers.stream()
                .anyMatch(
                        w -> w.getInterviewId() == interviewId
                                && w.isApprove()
                );
    }

    /**
     * Метод проверяет является, ли участник собеседования одобренным.
     *
     * @param interviewId Interview ID
     * @param userId      User ID
     * @param wishers     List<WisherDto>
     * @return boolean true or false
     */
    @Override
    public boolean isUserDismissed(int interviewId, int userId, List<WisherDto> wishers) {
        return wishers.stream()
                .anyMatch(
                        w -> w.getInterviewId() == interviewId
                                && w.getUserId() == userId
                                && w.isApprove()
                );
    }

    /**
     * Метод собирает статистику по всем коллекциям в карту
     * Map<Integer, InterviewStatistic>
     * key = interviewId,
     * value = InterviewStatistic calc.
     *
     * @param wishers List<WisherDTO>
     * @return Map<Integer, InterviewStatistic>
     */
    @Override
    public Map<Integer, InterviewStatistic> getInterviewStatistic(List<WisherDto> wishers) {
        Map<Integer, InterviewStatistic> result = new HashMap<>();
        for (WisherDto wisherDto : wishers) {
            var interviewId = wisherDto.getInterviewId();
            int participate = 1;
            int except = wisherDto.isApprove() ? 0 : 1;
            int passed = wisherDto.isApprove() ? 1 : 0;
            result.computeIfPresent(interviewId,
                    (key, oldValue) -> new InterviewStatistic(
                            oldValue.getParticipate() + participate,
                            oldValue.getExpect() + except,
                            oldValue.getPassed() + passed));
            result.putIfAbsent(interviewId, new InterviewStatistic(participate, except, passed));
        }
        return result;
    }

    /**
     * Метод выполняет подсчет количества откликов на участие в собеседовании
     *
     * @param wishers     wishers
     * @param interviewId interviewId
     * @return количество откликов
     */
    @Override
    public Long countWishers(List<WisherDto> wishers, int interviewId) {
        return wishers
                .stream()
                .map(WisherDto::getInterviewId)
                .filter(integer -> integer.equals(interviewId))
                .count();
    }

    /**
     * Метод для установки своего WebClient
     *
     * @param webClientWisher WebClient
     */
    public void setWebClientWisher(WebClient webClientWisher) {
        this.webClientWisher = webClientWisher;
    }
}