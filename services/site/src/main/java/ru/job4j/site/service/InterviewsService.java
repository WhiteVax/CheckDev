package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import ru.job4j.site.dto.FilterRequestParams;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.util.RestAuthCall;
import ru.job4j.site.util.RestPageImpl;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class InterviewsService {

    private final WisherService wisherService;
    private final EurekaUriProvider uriProvider;
    private static final String SERVICE_ID = "mock";
    private static final String DIRECT = "/interviews/";

    public Page<InterviewDTO> getAll(String token, int page, int size)
            throws JsonProcessingException {
        var text = new RestAuthCall(String
                .format("%s%s?page=%d&?size=%d", uriProvider.getUri(SERVICE_ID), DIRECT, page, size))
                .get(token);
        var mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        var pageType = mapper.getTypeFactory()
                .constructParametricType(RestPageImpl.class, InterviewDTO.class);
        return mapper.readValue(text, pageType);
    }

    public Page<InterviewDTO> getAllByUserIdRelated(String token, int page, int size, int userId)
            throws JsonProcessingException {
        var text = new RestAuthCall(String
                .format("%s%s/findByUserIdRelated/%s?page=%d&?size=%d",
                        uriProvider.getUri(SERVICE_ID), DIRECT, userId, page, size))
                .get(token);
        var mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        var pageType = mapper.getTypeFactory()
                .constructParametricType(RestPageImpl.class, InterviewDTO.class);
        return mapper.readValue(text, pageType);
    }

    public List<InterviewDTO> getLast() throws JsonProcessingException {
        String text = new RestAuthCall(String.format("%s%s/last", uriProvider.getUri(SERVICE_ID), DIRECT))
                .get();
        var mapper = new ObjectMapper();
        return mapper.readValue(text, new TypeReference<>() {
        });
    }

    public Page<InterviewDTO> getByTopicId(int topicId, int page, int size)
            throws JsonProcessingException {
        var text =
                new RestAuthCall(String
                        .format("%s%sfindByTopicId/%d?page=%d&size=%d",
                                uriProvider.getUri(SERVICE_ID), DIRECT, topicId, page, size)).get();
        var mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        var pageType = mapper.getTypeFactory()
                .constructParametricType(RestPageImpl.class, InterviewDTO.class);
        return mapper.readValue(text, pageType);
    }

    /**
     * Метод выполняет set поля countWishers(количество откликов) модели Интервью
     *
     * @param interviewsDTO interviewsDTO
     * @param token         token
     * @throws JsonProcessingException
     */
    public void setCountWishers(List<InterviewDTO> interviewsDTO, String token)
            throws JsonProcessingException {
        for (var interviewDTO : interviewsDTO) {
            var wishers = wisherService.getAllWisherDtoByInterviewId(
                    token, String.valueOf(interviewDTO.getId()));
            Long countWishers = wisherService.countWishers(wishers, interviewDTO.getId());
            interviewDTO.setCountWishers(countWishers);
        }
    }

    /**
     * Метод получает из REST сервиса MOCK все собеседования,
     * на которые пользователь должен оставить отзыв
     *
     * @param userId ID User
     * @return List<Interview>
     */
    public List<InterviewDTO> findAllIdByNoFeedback(int userId) {
        List<InterviewDTO> result = new ArrayList<>();
        String url = String.format("%s%snoFeedback/%d", uriProvider.getUri(SERVICE_ID), DIRECT, userId);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonText = new RestAuthCall(url).get();
            result = mapper.readValue(jsonText, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("MOCK API is not available, error: {}", e.getMessage());
        }
        return result;
    }

    /**
     * Метод получает из REST сервиса MOCK все новые собеседования,
     *
     * @return List<Interview>
     */
    public List<InterviewDTO> getNewInterviews() {
        List<InterviewDTO> result = new ArrayList<>();
        String url = String.format("%s%sinterviewStatusNew", uriProvider.getUri(SERVICE_ID), DIRECT);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonText = new RestAuthCall(url).get();
            result = mapper.readValue(jsonText, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("MOCK API is not available, error: {}", e.getMessage());
        }
        return result;
    }

    /**
     * Метод выполняет подсчет количества интервью по topicId
     *
     * @param topicId topicId
     * @return количество интервью по topicId
     */
    public Long countNewInterviewsByTopic(int topicId) {
        List<InterviewDTO> list = getNewInterviews();
        return list.stream().map(InterviewDTO::getTopicId)
                .filter(integer -> integer.equals(topicId)).count();
    }

    public Page<InterviewDTO> getAllWithFilters(
            FilterRequestParams filterRequestParams, int page, int size)
            throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var headers = new HttpHeaders();
        headers.add("filter-request-params", mapper.writeValueAsString(filterRequestParams));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        var text = new RestAuthCall(
                String.format("%s%sgetInterviews?page=%d&size=%d",
                        uriProvider.getUri(SERVICE_ID), DIRECT, page, size)).getWithHeaders(headers);
        var pageType = mapper.getTypeFactory()
                .constructParametricType(RestPageImpl.class, InterviewDTO.class);
        return mapper.readValue(text, pageType);
    }
}
