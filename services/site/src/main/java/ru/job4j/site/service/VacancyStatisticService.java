package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.job4j.site.domain.VacancyStatistic;
import ru.job4j.site.dto.DirectionKey;
import ru.job4j.site.dto.VacancyStatisticWithDates;
import ru.job4j.site.util.RestAuthCall;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VacancyStatisticService {

    private final EurekaUriProvider uriProvider;
    private static final String SERVICE_ID = "generator";
    private static final String DIRECT = "/statistic/";

    public void create(String token, DirectionKey directionKey) throws JsonProcessingException {
        String uri = String.format("%s%s%s", uriProvider.getUri(SERVICE_ID), DIRECT, "create");
        new RestAuthCall(uri).post(token, new ObjectMapper().writeValueAsString(directionKey));
    }

    public VacancyStatisticWithDates getAll() {
        return getRequest(String.format("%s%s%s",
                uriProvider.getUri(SERVICE_ID), DIRECT, "get"));
    }

    public void update(String token, DirectionKey directionKey) throws JsonProcessingException {
        var json = new ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(directionKey);
        new RestAuthCall(String
                .format("%s%s%s", uriProvider.getUri(SERVICE_ID), DIRECT, "update"))
                .update(token, json);
    }

    public VacancyStatisticWithDates renew() {
        return getRequest(String
                .format("%s%s%s", uriProvider.getUri(SERVICE_ID), DIRECT, "renew"));
    }

    public void delete(int id) {
        new RestTemplate().delete(String
                .format("%s%s%s%d", uriProvider.getUri(SERVICE_ID), DIRECT, "delete/", id));
    }

    private VacancyStatisticWithDates getRequest(String uri) {
        VacancyStatisticWithDates result =
                new VacancyStatisticWithDates(List.of(), new VacancyStatisticWithDates.Dates());
        var text = new RestAuthCall(uri).get();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            objectMapper.registerModule(javaTimeModule);
            result = objectMapper.readValue(text, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return result;
    }
}
