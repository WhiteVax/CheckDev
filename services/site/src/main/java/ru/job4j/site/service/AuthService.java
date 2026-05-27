package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.job4j.site.dto.ProfileDTO;
import ru.job4j.site.dto.UserInfoDTO;
import ru.job4j.site.util.RestAuthCall;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final EurekaUriProvider uriProvider;
    private static final String SERVICE_ID = "auth";

    public UserInfoDTO userInfo(String token) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new RestAuthCall(String
                .format("%s/person/current", uriProvider.getUri(SERVICE_ID))
        ).get(token), UserInfoDTO.class);
    }

    public String token(Map<String, String> params) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            result = mapper.readTree(
                    new RestAuthCall(String
                            .format("%s/oauth/token", uriProvider.getUri(SERVICE_ID)))
                            .token(params)
            ).get("access_token").asText();
        } catch (Exception e) {
            log.error("Get token from service Auth error: {}", e.getMessage());
        }
        return result;
    }

    /**
     * Метод проверяет доступность сервера Auth.
     *
     * @return String body
     */
    public boolean getPing() {
        var result = false;
        try {
            result = !new RestAuthCall(String
                    .format("%s/ping", uriProvider.getUri(SERVICE_ID)))
                    .get().isEmpty();
        } catch (Exception e) {
            log.error("Get PING from API Auth error: {}", e.getMessage());
        }
        return result;
    }

    public ProfileDTO findById(int id) throws JsonProcessingException {
        var text = new RestAuthCall(String
                .format("%s/profiles/%d", uriProvider.getUri(SERVICE_ID), id)).get();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(text, new TypeReference<>() {
        });
    }
}
