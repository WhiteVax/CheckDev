package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.dto.UserInfoDTO;
import ru.job4j.site.dto.WisherDetailDTO;
import ru.job4j.site.dto.WisherDto;
import ru.job4j.site.enums.StatusInterview;
import ru.job4j.site.util.RestAuthCall;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewService {

    private final ProfilesService profilesService;
    private final EurekaUriProvider uriProvider;
    private static final String SERVICE_ID = "mock";
    private static final String DIRECT = "/interview/";

    public InterviewDTO create(String token, InterviewDTO interviewDTO) throws JsonProcessingException {
        interviewDTO.setStatusId(StatusInterview.IS_NEW.getId());
        var mapper = new ObjectMapper();
        var out = new RestAuthCall(String
                .format("%s%s", uriProvider.getUri(SERVICE_ID), DIRECT)).post(
                token,
                mapper.writeValueAsString(interviewDTO)
        );
        return mapper.readValue(out, InterviewDTO.class);
    }

    public InterviewDTO getById(String token, int id) throws JsonProcessingException {
        var text = new RestAuthCall(String
                .format("%s%s%d", uriProvider.getUri(SERVICE_ID), DIRECT, id))
                .get(token);
        return new ObjectMapper().readValue(text, new TypeReference<>() {
        });
    }

    public void update(String token, InterviewDTO interviewDTO) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        new RestAuthCall(String
                .format("%s%s", uriProvider.getUri(SERVICE_ID), DIRECT)).update(
                token,
                mapper.writeValueAsString(interviewDTO));
    }

    /**
     * Метод обновляет статус собеседования
     *
     * @param token        User security token
     * @param interviewDTO InterviewDTO
     */
    public void updateStatus(String token, InterviewDTO interviewDTO) {
        try {
            var mapper = new ObjectMapper();
            new RestAuthCall(String
                    .format("%s%sstatus/", uriProvider.getUri(SERVICE_ID), DIRECT)).put(
                    token,
                    mapper.writeValueAsString(interviewDTO));
        } catch (Exception e) {
            log.error("API service MOCK not found, error: {}", e.getMessage());
        }
    }

    /**
     * Метод проверяет являться пользователь автором собеседования.
     *
     * @param userInfoDTO  UserInfoDto
     * @param interviewDTO InterviewDTO
     * @return boolean userId == submitterId
     */
    public boolean isAuthor(UserInfoDTO userInfoDTO, InterviewDTO interviewDTO) {
        return userInfoDTO.getId() == interviewDTO.getSubmitterId();
    }

    /**
     * Метод формирует детальную информацию по всем кандидатам в собеседовании
     *
     * @param wishers List<WisherDto>
     * @return List<WisherDetail>
     */
    public List<WisherDetailDTO> getAllWisherDetail(List<WisherDto> wishers) {
        List<WisherDetailDTO> wishersDetail = new ArrayList<>();
        for (WisherDto wisherDto : wishers) {
            var person = profilesService.getProfileById(wisherDto.getUserId());
            if (person.isPresent()) {
                var wisherUser = new WisherDetailDTO(wisherDto.getId(),
                        wisherDto.getInterviewId(),
                        wisherDto.getUserId(),
                        person.get().getUsername(),
                        wisherDto.getContactBy(),
                        wisherDto.isApprove());
                wishersDetail.add(wisherUser);
            }
        }
        return wishersDetail;
    }
}
