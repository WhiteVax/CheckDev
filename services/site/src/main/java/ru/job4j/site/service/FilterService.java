package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.site.domain.FilterProfile;
import ru.job4j.site.dto.FilterDTO;
import ru.job4j.site.util.RestAuthCall;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilterService {

    private final EurekaUriProvider uriProvider;
    private static final String SERVICE_ID = "mock";
    private static final String DIRECT = "/filter/";

    public FilterDTO save(String token, FilterDTO filter) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var out = new RestAuthCall(String
                .format("%s%s", uriProvider.getUri(SERVICE_ID), DIRECT))
                .post(
                token,
                mapper.writeValueAsString(filter)
        );
        return mapper.readValue(out, FilterDTO.class);
    }

    public FilterDTO getByUserId(String token, int userId) throws JsonProcessingException {
        var text = new RestAuthCall(String
                .format("%s%s%d", uriProvider.getUri(SERVICE_ID), DIRECT, userId))
                .get(token);
        return new ObjectMapper().readValue(text, new TypeReference<>() {
        });
    }

    public void deleteByUserId(String token, int userId) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        new RestAuthCall(String
                .format("%s%sdelete/%d", uriProvider.getUri(SERVICE_ID), DIRECT, userId))
                .delete(
                token,
                mapper.writeValueAsString(userId)
        );
    }

    public List<FilterProfile> getProfiles() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var text = new RestAuthCall(String
                .format("%s%sprofiles", uriProvider.getUri(SERVICE_ID), DIRECT))
                .get();
        return mapper.readValue(text, new TypeReference<>() {
        });
    }

    public String getNameById(Collection<FilterProfile> filterProfiles, int id) {
        String result = "";
        for (var filterProfile : filterProfiles) {
            if (filterProfile.getId() == id) {
                result = filterProfile.getName();
                break;
            }
        }
        return result;
    }
}
