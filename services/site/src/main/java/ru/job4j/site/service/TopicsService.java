package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.job4j.site.dto.*;
import ru.job4j.site.util.RestAuthCall;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicsService {

    private final InterviewsService interviewsService;
    private final EurekaUriProvider uriProvider;
    private static final String SERVICE_ID = "desc";
    private static final String DIRECT_SINGLE = "/topic/";
    private static final String DIRECT_MULTIPLE = "/topics/";

    public List<TopicDTO> getByCategory(int id) throws JsonProcessingException {
        var text = new RestAuthCall(String
                .format("%s%s%d", uriProvider.getUri(SERVICE_ID), DIRECT_MULTIPLE, id)).get();
        var mapper = new ObjectMapper();
        return mapper.readValue(text, new TypeReference<>() {
        });
    }

    public TopicDTO getById(int id) throws JsonProcessingException {
        var text = new RestAuthCall(String
                .format("%s%s%d", uriProvider.getUri(SERVICE_ID), DIRECT_SINGLE, id)).get();
        var mapper = new ObjectMapper();
        return mapper.readValue(text, new TypeReference<>() {
        });
    }

    public TopicDTO create(String token, TopicLiteDTO topicLite) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var topic = new TopicDTO();
        topic.setName(topicLite.getName());
        topic.setPosition(topicLite.getPosition());
        topic.setText(topicLite.getText());
        var category = new CategoryDTO();
        category.setId(topicLite.getCategoryId());
        topic.setCategory(category);
        var text = String.format("%s%s", uriProvider.getUri(SERVICE_ID), DIRECT_SINGLE);
        var out = new RestAuthCall(text).post(
                token,
                mapper.writeValueAsString(topic)
        );
        return mapper.readValue(out, TopicDTO.class);
    }

    public void update(String token, TopicDTO topic) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        topic.setUpdated(Calendar.getInstance());
        var json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(topic);
        var text = String.format("%s%s", uriProvider.getUri(SERVICE_ID), DIRECT_SINGLE);
        new RestAuthCall(text).update(
                token,
                json
        );
    }

    public void delete(String token, int id) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var topic = new TopicDTO();
        topic.setId(id);
        var text = String.format("%s%s", uriProvider.getUri(SERVICE_ID), DIRECT_SINGLE);
        new RestAuthCall(text).delete(
                token,
                mapper.writeValueAsString(topic)
        );
    }

    public String getNameById(int id) {
        var text = String
                .format("%s%sname/%d", uriProvider.getUri(SERVICE_ID), DIRECT_SINGLE, id);
        return new RestAuthCall(text).get();
    }

    public CategoryIdNameDTO getCategoryIdNameDTOByTopicId(int topicId)
            throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var text = String
                .format("%s%scategoryIdName/%d", uriProvider.getUri(SERVICE_ID), DIRECT_SINGLE, topicId);
        return mapper.readValue(text, CategoryIdNameDTO.class);
    }

    public List<TopicIdNameDTO> getTopicIdNameDtoByCategory(int categoryId)
            throws JsonProcessingException {
        var text = new
                RestAuthCall(String.format("%s%sgetByCategoryId/%d",
                uriProvider.getUri(SERVICE_ID), DIRECT_MULTIPLE, categoryId)).get();
        var mapper = new ObjectMapper();
        return mapper.readValue(text, new TypeReference<>() {
        });
    }

    /**
     * Метод возвращает все Topic в виде TopicLiteDTO из сервиса DESC
     *
     * @return List<TopicLiteDTO>
     */
    public List<TopicLiteDTO> getAllTopicLiteDTO() {
        List<TopicLiteDTO> result = new ArrayList<>();
        try {
            var text = new RestAuthCall(String
                    .format("%s%sdto/lite", uriProvider.getUri(SERVICE_ID), DIRECT_MULTIPLE)).get();
            var mapper = new ObjectMapper();
            result = mapper.readValue(text, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Request to API DESC error: {}", e.getMessage());
        }
        return result;
    }

    /**
     * Метод возвращает Optional<TopicLiteDto> по Topic ID
     *
     * @param topicId ID Topic
     * @return Optional<Topic>
     */
    public Optional<TopicLiteDTO> getTopicLiteDTOById(int topicId) {
        Optional<TopicLiteDTO> result = Optional.empty();
        try {
            var url = String
                    .format("%s%sdto/lite/%d", uriProvider.getUri(SERVICE_ID), DIRECT_SINGLE, topicId);
            var text = new RestAuthCall(url).get();
            var mapper = new ObjectMapper();
            TopicLiteDTO topicLiteDto = mapper.readValue(text, new TypeReference<>() {
            });
            result = Optional.of(topicLiteDto);
        } catch (Exception e) {
            log.error("Request to API DESC error: {}", e.getMessage());
        }
        return result;
    }

    /**
     * Метод преобразовывает список TopicLiteDto в Map<Integer, TopicLiteDto>
     * key = Topic.ID
     * value = TopicLiteDTO
     *
     * @param liteDTOS List<TopicLiteDTO>
     * @return Map<Integer, TopicLiteDTO>
     */
    public Map<Integer, TopicLiteDTO> liteDTTOSToMap(List<TopicLiteDTO> liteDTOS) {
        return liteDTOS.stream()
                .collect(Collectors
                        .toMap(TopicLiteDTO::getId,
                                Function.identity()));
    }

    /**
     * Метод получает List Topic, c передачей в поле количеств интервью для определенного Topic Id.
     *
     * @param categoryId categoryId
     * @return List Topic
     * @throws JsonProcessingException
     */
    public List<TopicDTO> getTopicsWithCountInterview(int categoryId) throws JsonProcessingException {
        List<TopicDTO> topicByCategory = getByCategory(categoryId);
        for (var topic : topicByCategory) {
            topic.setCountInterview(interviewsService.countNewInterviewsByTopic(topic.getId()));
        }
        return topicByCategory;
    }
}
