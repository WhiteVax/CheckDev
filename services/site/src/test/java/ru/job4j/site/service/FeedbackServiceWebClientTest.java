package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.job4j.site.dto.FeedbackDTO;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.enums.StatusInterview;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * CheckDev пробное собеседование
 *
 * @author Dmitry Stepanov
 * @version 29.10.2023 03:14
 */
@SpringBootTest(classes = FeedbackServiceWebClient.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class FeedbackServiceWebClientTest {

    private final String urlFeedback = "/feedback/";

    @Mock
    private WebClient webClientMock;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersMock;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriMock;
    @Mock
    private WebClient.RequestBodySpec requestBodyMock;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriMock;
    @Mock
    private WebClient.ResponseSpec responseMock;
    @MockBean
    private InterviewService interviewService;
    @MockBean
    private NotificationService notificationService;
    @MockBean
    private EurekaUriProvider uriProvider;

    private FeedbackServiceWebClient feedbackService;


    @BeforeEach
    void setUp() {
        feedbackService = new FeedbackServiceWebClient(interviewService, notificationService, uriProvider);
        feedbackService.setWebClientFeedback(webClientMock);
    }

    @Test
    void initInjectedNotNul() {
        assertThat(interviewService).isNotNull();
        assertThat(notificationService).isNotNull();
        assertThat(feedbackService).isNotNull();
        assertThat(uriProvider).isNotNull();
    }

    @Test
    void whenSaveThenReturnTrue() throws JsonProcessingException {
        var interviewDto = InterviewDTO.of()
                .id(1)
                .submitterId(1)
                .statusId(StatusInterview.IS_FEEDBACK.getId())
                .topicId(1)
                .mode(1)
                .build();
        var feedbackDto1 = new FeedbackDTO(1, interviewDto.getId(), interviewDto.getSubmitterId(), 0, "text", 5);
        var token = "1234";
        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock
                .uri(
                        String.format("%s?iId=%d&uId=%d",
                                urlFeedback, feedbackDto1.getInterviewId(), feedbackDto1.getUserId())))
                .thenReturn(requestBodyMock);
        when(requestBodyMock.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodyMock);
        when(requestBodyMock.retrieve()).thenReturn(responseMock);
        when(responseMock.toEntityList(FeedbackDTO.class)).thenReturn(Mono.empty());
        when(webClientMock.post()).thenReturn(requestBodyUriMock);
        when(requestBodyUriMock.uri(urlFeedback)).thenReturn(requestBodyMock);
        when(requestBodyMock.header("Authorization", "Bearer " + token)).thenReturn(requestBodyMock);
        when(requestBodyMock.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodyMock);
        when(requestBodyMock.bodyValue(feedbackDto1)).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.toEntity(FeedbackDTO.class)).thenReturn(Mono.just(new ResponseEntity<>(feedbackDto1, HttpStatus.CREATED)));
        when(interviewService.getById(token, feedbackDto1.getInterviewId())).thenReturn(interviewDto);
        doNothing().when(interviewService).updateStatus(token, interviewDto);
        var actual = feedbackService.save(token, feedbackDto1, "vasya");
        assertThat(actual).isTrue();
    }

    @Test
    void whenSaveFeedbackThenReturnFalse() throws JsonProcessingException {
        var interviewDto = new InterviewDTO();
        interviewDto.setId(1);
        interviewDto.setSubmitterId(1);
        interviewDto.setMode(1);
        var feedbackDto1 = new FeedbackDTO(1, interviewDto.getId(), interviewDto.getSubmitterId(), 0, "text", 5);
        var token = "1234";
        when(webClientMock.post()).thenReturn(requestBodyUriMock);
        when(requestBodyUriMock.uri(urlFeedback)).thenReturn(requestBodyMock);
        when(requestBodyMock.header("Authorization", "Bearer " + token)).thenReturn(requestBodyMock);
        when(requestBodyMock.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodyMock);
        when(requestBodyMock.bodyValue(feedbackDto1)).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.toEntity(FeedbackDTO.class)).thenReturn(Mono.empty());
        when(interviewService.getById(token, feedbackDto1.getInterviewId())).thenReturn(interviewDto);
        var actual = feedbackService.save(token, feedbackDto1, "vasya");
        assertThat(actual).isFalse();
    }

    @Test
    void whenFindFeedbackByInterviewIdThenReturnListFeedBackDto() {
        var feedbackDto1 = new FeedbackDTO(1, 1, 1, 1, "text", 5);
        var feedbackDto2 = new FeedbackDTO(2, 1, 2, 2, "text2", 5);
        var feedbacks = List.of(feedbackDto1, feedbackDto2);
        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri(urlFeedback + feedbackDto1.getInterviewId())).thenReturn(requestBodyMock);
        when(requestBodyMock.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodyMock);
        when(requestBodyMock.retrieve()).thenReturn(responseMock);
        when(responseMock.toEntityList(FeedbackDTO.class)).thenReturn(Mono.just(new ResponseEntity<>(feedbacks, HttpStatus.OK)));
        var actual = feedbackService.findByInterviewId(feedbackDto1.getInterviewId());
        assertThat(actual).isEqualTo(feedbacks);
    }

    @Test
    void whenFindFeedbackByInterviewIdThenReturnEmptyList() {
        var interviewId = 1;
        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri(urlFeedback + interviewId)).thenReturn(requestBodyMock);
        when(requestBodyMock.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodyMock);
        when(requestBodyMock.retrieve()).thenReturn(responseMock);
        when(responseMock.toEntityList(FeedbackDTO.class)).thenReturn(Mono.empty());
        var actual = feedbackService.findByInterviewId(interviewId);
        assertThat(actual).isEmpty();
    }

    @Test
    void whenFindByInterviewIdAndUserIdThenReturnFeedbackDtoList() {
        var feedbackDto1 = new FeedbackDTO(1, 1, 1, 1, "text", 5);
        var feedbackDto2 = new FeedbackDTO(2, 1, 1, 2, "text2", 5);
        var feedbacks = List.of(feedbackDto1, feedbackDto2);
        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock
                .uri(
                        String.format("%s?iId=%d&uId=%d",
                                urlFeedback, feedbackDto1.getInterviewId(), feedbackDto1.getUserId())))
                .thenReturn(requestBodyMock);
        when(requestBodyMock.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodyMock);
        when(requestBodyMock.retrieve()).thenReturn(responseMock);
        when(responseMock.toEntityList(FeedbackDTO.class)).thenReturn(Mono.just(new ResponseEntity<>(feedbacks, HttpStatus.OK)));
        var actual = feedbackService.findByInterviewIdAndUserId(feedbackDto1.getInterviewId(), feedbackDto1.getUserId());
        assertThat(actual).isEqualTo(feedbacks);
    }

    @Test
    void whenFindByInterviewIdAndUserIdThenReturnEmptyList() {
        var feedbackDto1 = new FeedbackDTO(1, 1, 1, 1, "text", 5);
        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock
                .uri(
                        String.format("%s?iId=%d&uId=%d",
                                urlFeedback, feedbackDto1.getInterviewId(), feedbackDto1.getUserId())))
                .thenReturn(requestBodyMock);
        when(requestBodyMock.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodyMock);
        when(requestBodyMock.retrieve()).thenReturn(responseMock);
        when(responseMock.toEntityList(FeedbackDTO.class)).thenReturn(Mono.empty());
        var actual = feedbackService.findByInterviewIdAndUserId(feedbackDto1.getInterviewId(), feedbackDto1.getUserId());
        assertThat(actual).isEmpty();
    }

    @Test
    void whenGerRoleInInterviewThenReturnInterviewMode() {
        var interview = new InterviewDTO();
        interview.setSubmitterId(1);
        interview.setMode(1);
        var userId = interview.getSubmitterId();
        var expected = interview.getMode();
        var actual = feedbackService.gerRoleInInterview(userId, interview);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void whenGerRoleInInterviewThenReturnRoleTwo() {
        var interview = new InterviewDTO();
        interview.setSubmitterId(1);
        interview.setMode(1);
        var userId = 2;
        var expected = 2;
        var actual = feedbackService.gerRoleInInterview(userId, interview);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void whenGerRoleInInterviewThenReturnRoleOne() {
        var interview = new InterviewDTO();
        interview.setSubmitterId(1);
        interview.setMode(2);
        var userId = 2;
        var expected = 1;
        var actual = feedbackService.gerRoleInInterview(userId, interview);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void whenUpdateStatusInterviewThenReturnIDStatusIsFeedback() {
        var token = "1234";
        var interviewDto = InterviewDTO.of()
                .id(1)
                .submitterId(1)
                .statusId(StatusInterview.IN_PROGRESS.getId())
                .topicId(1)
                .mode(1)
                .build();
        var feedbackDto1 = new FeedbackDTO(1, 1, 1, 1, "text", 5);
        doNothing().when(interviewService).updateStatus(token, interviewDto);
        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock
                .uri(
                        String.format("%s?iId=%d&uId=%d",
                                urlFeedback, feedbackDto1.getInterviewId(), feedbackDto1.getUserId())))
                .thenReturn(requestBodyMock);
        when(requestBodyMock.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodyMock);
        when(requestBodyMock.retrieve()).thenReturn(responseMock);
        when(responseMock.toEntityList(FeedbackDTO.class)).thenReturn(Mono.empty());
        var actual = feedbackService.updateStatusInterview(token, interviewDto, feedbackDto1.getUserId());
        assertThat(actual).isEqualTo(StatusInterview.IS_FEEDBACK.getId());
    }

    @Test
    void whenUpdateStatusInterviewThenReturnIdOldStatus() {
        var token = "1234";
        var interviewDto = InterviewDTO.of()
                .id(1)
                .submitterId(1)
                .statusId(StatusInterview.IS_NEW.getId())
                .topicId(1)
                .mode(1)
                .build();
        var feedbackDto1 = new FeedbackDTO(1, 1, 1, 1, "text", 5);
        doNothing().when(interviewService)
                .updateStatus(token, interviewDto);
        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock
                .uri(
                        String.format("%s?iId=%d&uId=%d",
                                urlFeedback, feedbackDto1.getInterviewId(), feedbackDto1.getUserId())))
                .thenReturn(requestBodyMock);
        when(requestBodyMock.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodyMock);
        when(requestBodyMock.retrieve()).thenReturn(responseMock);
        when(responseMock.toEntityList(FeedbackDTO.class)).thenReturn(Mono.empty());
        var actual = feedbackService.updateStatusInterview(token, interviewDto, feedbackDto1.getUserId());
        assertThat(actual).isEqualTo(interviewDto.getStatusId());
    }

    @Test
    void whenUpdateStatusInterviewIsFeedbackThenReturnIdStatusFeedback() {
        var token = "1234";
        var interviewDto = InterviewDTO.of()
                .id(1)
                .submitterId(1)
                .statusId(StatusInterview.IS_FEEDBACK.getId())
                .topicId(1)
                .mode(1)
                .build();
        var feedbackDto1 = new FeedbackDTO(1, 1, 1, 1, "text", 5);
        doNothing().when(interviewService).updateStatus(token, interviewDto);
        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock
                .uri(
                        String.format("%s?iId=%d&uId=%d",
                                urlFeedback, feedbackDto1.getInterviewId(), feedbackDto1.getUserId())))
                .thenReturn(requestBodyMock);
        when(requestBodyMock.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodyMock);
        when(requestBodyMock.retrieve()).thenReturn(responseMock);
        when(responseMock.toEntityList(FeedbackDTO.class)).thenReturn(Mono.empty());
        var actual = feedbackService.updateStatusInterview(token, interviewDto, feedbackDto1.getUserId());
        assertThat(actual).isEqualTo(StatusInterview.IS_FEEDBACK.getId());
    }

    @Test
    void whenUpdateStatusInterviewIsFeedbackAndFeedbackTwoThenReturnIdStatusFeedback() {
        var token = "1234";
        var interviewDto = InterviewDTO.of()
                .id(1)
                .submitterId(1)
                .statusId(StatusInterview.IS_FEEDBACK.getId())
                .topicId(1)
                .mode(1)
                .build();
        var feedbackDto1 = new FeedbackDTO(1, 1, 1, 1, "text", 5);
        var feedbackDto2 = new FeedbackDTO(2, 1, 1, 2, "text2", 5);
        var feedbacks = List.of(feedbackDto1, feedbackDto2);
        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock
                .uri(
                        String.format("%s?iId=%d&uId=%d",
                                urlFeedback, feedbackDto1.getInterviewId(), feedbackDto1.getUserId())))
                .thenReturn(requestBodyMock);
        when(requestBodyMock.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodyMock);
        when(requestBodyMock.retrieve()).thenReturn(responseMock);
        when(responseMock.toEntityList(FeedbackDTO.class)).thenReturn(Mono.just(new ResponseEntity<>(feedbacks, HttpStatus.OK)));
        var actual = feedbackService.updateStatusInterview(token, interviewDto, feedbackDto1.getUserId());
        assertThat(actual).isEqualTo(StatusInterview.IS_FEEDBACK.getId());
    }

    @Test
    void whenUpdateStatusInterviewIsFeedbackThenReturnIdStatusCompleted() {
        var token = "1234";
        var interviewDto = InterviewDTO.of()
                .id(1)
                .submitterId(1)
                .statusId(StatusInterview.IS_FEEDBACK.getId())
                .topicId(1)
                .mode(1)
                .build();
        var feedbackDto1 = new FeedbackDTO(1, 1, 1, 1, "text", 5);
        var feedbacks = List.of(feedbackDto1);
        doNothing().when(interviewService)
                .updateStatus(token, interviewDto);
        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock
                .uri(
                        String.format("%s?iId=%d&uId=%d",
                                urlFeedback, feedbackDto1.getInterviewId(), feedbackDto1.getUserId())))
                .thenReturn(requestBodyMock);
        when(requestBodyMock.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodyMock);
        when(requestBodyMock.retrieve()).thenReturn(responseMock);
        when(responseMock.toEntityList(FeedbackDTO.class)).thenReturn(Mono.just(new ResponseEntity<>(feedbacks, HttpStatus.OK)));
        var actual = feedbackService.updateStatusInterview(token, interviewDto, feedbackDto1.getUserId());
        assertThat(actual).isEqualTo(StatusInterview.IS_COMPLETED.getId());
    }

    @Test
    void whenFeedbackDTOSToListThenReturnEmptyMap() {
        var actual = feedbackService.feedbackDTOSToMap(Collections.emptyList());
        assertThat(actual).isEmpty();
    }

    @Test
    void whenFeedbackDTOSToListThenReturnMapSizeOne() {
        var feedbackDto = FeedbackDTO.of()
                .id(1)
                .interviewId(2)
                .userId(3)
                .roleInInterview(1)
                .textFeedback("text")
                .scope(5)
                .build();
        var lestFeedbackDTO = List.of(feedbackDto);
        var expectMap = Map.of(
                feedbackDto.getUserId(),
                lestFeedbackDTO
        );
        var actualMap = feedbackService.feedbackDTOSToMap(lestFeedbackDTO);
        assertThat(actualMap)
                .usingRecursiveComparison()
                .isEqualTo(expectMap);
    }

    @Test
    void whenFeedbackDTOSToListThenReturnMapSizeOneValueSizeTwo() {
        var feedbackDto1 = FeedbackDTO.of()
                .id(1)
                .interviewId(2)
                .userId(3)
                .roleInInterview(1)
                .textFeedback("text1")
                .scope(5)
                .build();
        var feedbackDto2 = FeedbackDTO.of()
                .id(2)
                .interviewId(2)
                .userId(3)
                .roleInInterview(2)
                .textFeedback("text2")
                .scope(3)
                .build();
        var lestFeedbackDTO = List.of(feedbackDto1, feedbackDto2);
        var expectMap = Map.of(
                feedbackDto1.getUserId(), lestFeedbackDTO
        );
        var actualMap = feedbackService.feedbackDTOSToMap(lestFeedbackDTO);
        assertThat(actualMap)
                .usingRecursiveComparison()
                .isEqualTo(expectMap);
    }

    @Test
    void whenFeedbackDTOSToListThenReturnMapTwo() {
        var feedbackDto1 = FeedbackDTO.of()
                .id(1)
                .interviewId(2)
                .userId(3)
                .roleInInterview(1)
                .textFeedback("text1")
                .scope(5)
                .build();
        var feedbackDto2 = FeedbackDTO.of()
                .id(2)
                .interviewId(2)
                .userId(4)
                .roleInInterview(2)
                .textFeedback("text2")
                .scope(3)
                .build();
        var lestFeedbackDTO = List.of(feedbackDto1, feedbackDto2);
        var expectMap = Map.of(
                feedbackDto1.getUserId(), List.of(feedbackDto1),
                feedbackDto2.getUserId(), List.of(feedbackDto2)
        );
        var actualMap = feedbackService.feedbackDTOSToMap(lestFeedbackDTO);
        assertThat(actualMap)
                .usingRecursiveComparison()
                .isEqualTo(expectMap);
    }
}