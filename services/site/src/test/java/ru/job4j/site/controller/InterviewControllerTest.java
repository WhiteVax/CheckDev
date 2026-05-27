package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.job4j.site.SiteSrv;
import ru.job4j.site.domain.Breadcrumb;
import ru.job4j.site.dto.*;
import ru.job4j.site.enums.StatusInterview;
import ru.job4j.site.service.*;
import ru.job4j.site.service.EurekaUriProvider;

import java.util.*;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SiteSrv.class)
@AutoConfigureMockMvc
public class InterviewControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private InterviewService interviewService;
    @MockBean
    private InterviewsService interviewsService;
    @MockBean
    private TopicsService topicsService;
    @MockBean
    private AuthService authService;
    @MockBean
    private WisherService wisherService;
    @MockBean
    private FeedbackService feedbackService;
    @MockBean
    private EurekaUriProvider uriProvider;

    @Test
    public void whenShowDetails() throws Exception {
        var token = "1410";
        var userInfo = new UserInfoDTO();
        var profileDTO = new ProfileDTO();
        InterviewDTO interview = new InterviewDTO();
        interview.setId(1);
        interview.setTitle("Some interview");
        interview.setAdditional("Some description");
        interview.setMode(4);
        interview.setTopicId(1);
        List<WisherDto> wishersDto = new ArrayList<>();
        TopicLiteDTO topicLiteDTO = new TopicLiteDTO(1, "nameTopic", "text",
                2, "categoryName", 15);
        var breadcrumbs = List.of(
                new Breadcrumb("Главная", "/index"),
                new Breadcrumb("Собеседования", "/interviews/"),
                new Breadcrumb(
                        String.format("%s / %s", topicLiteDTO.getCategoryName(), topicLiteDTO.getName()),
                        "/interview/" + interview.getId()));
        when(topicsService.getTopicLiteDTOById(topicLiteDTO.getId())).thenReturn(Optional.of(topicLiteDTO));
        when(authService.userInfo(token)).thenReturn(userInfo);
        when(authService.findById(interview.getSubmitterId())).thenReturn(profileDTO);
        when(interviewService.getById(token, 1)).thenReturn(interview);
        when(interviewService.isAuthor(userInfo, interview)).thenReturn(false);
        when(wisherService.getAllWisherDtoByInterviewId(token, String.valueOf(interview.getId())))
                .thenReturn(wishersDto);
        when(wisherService.getInterviewStatistic(wishersDto)).thenReturn(new HashMap<>());
        when(wisherService.isWisher(userInfo.getId(), interview.getId(), wishersDto)).thenReturn(false);
        when(feedbackService.findByInterviewId(interview.getId())).thenReturn(Collections.emptyList());
        when(uriProvider.getUri(Mockito.anyString())).thenReturn("https://service");
        mockMvc.perform(get("/interview/{id}", interview.getId())
                        .sessionAttr("token", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("interview", interview))
                .andExpect(model().attribute("authService", authService))
                .andExpect(model().attribute("breadcrumbs", breadcrumbs))
                .andExpect(model().attribute("userInfo", userInfo))
                .andExpect(model().attribute("isAuthor", false))
                .andExpect(model().attribute("isWisher", false))
                .andExpect(model().attribute("statisticMap", new HashMap<>()))
                .andExpect(model().attribute("STATUS_IN_PROGRESS_ID", StatusInterview.IN_PROGRESS.getId()))
                .andExpect(model().attribute("STATUS_IS_FEEDBACK_ID", StatusInterview.IS_FEEDBACK.getId()))
                .andExpect(model().attribute("topicLiteDTO", topicLiteDTO))
                .andExpect(model().attribute("feedbackMap", Collections.emptyMap()))
                .andExpect(view().name("interview/details"));
    }

    @Test
    public void whenOpenCreateForm() throws Exception {
        var breadcrumbs = List.of(
                new Breadcrumb("Главная", "/index"),
                new Breadcrumb("Категории", "/categories/"),
                new Breadcrumb("Some category", "/topics/1"),
                new Breadcrumb("Some topic", "/topic/1"),
                new Breadcrumb("Создание собеседования", String.format("/interview/createForm?topicId=%d", 1)));
        var token = "1410";
        var userInfo = new UserInfoDTO();
        var topic = new TopicDTO();
        topic.setId(1);
        topic.setName("Some topic");
        topic.setText("Some text");
        topic.setCreated(Calendar.getInstance());
        topic.setUpdated(Calendar.getInstance());
        CategoryDTO category = new CategoryDTO();
        category.setId(1);
        category.setName("Some category");
        topic.setCategory(category);
        when(authService.userInfo(token)).thenReturn(userInfo);
        when(topicsService.getById(1)).thenReturn(topic);
        when(interviewsService.findAllIdByNoFeedback(1)).thenReturn(Collections.emptyList());
        when(uriProvider.getUri(Mockito.anyString())).thenReturn("https://service");
        mockMvc.perform(get("/interview/createForm")
                        .sessionAttr("token", token)
                        .param("topicId", "1"))
                .andDo(print())
                .andExpect(model().attribute("noFeedback", Collections.emptyList()))
                .andExpect(model().attribute("category", topic.getCategory()))
                .andExpect(model().attribute("topic", topic))
                .andExpect(model().attribute("breadcrumbs", breadcrumbs))
                .andExpect(status().isOk())
                .andExpect(view().name("interview/createForm"));
    }

    @Test
    public void whenInterviewCreated() throws Exception {
        var token = "1410";
        var userInfo = new UserInfoDTO();
        var role = new Role();
        role.setId(1);
        role.setValue("ROLE_USER");
        userInfo.setRoles(List.of(role));
        InterviewDTO interview = new InterviewDTO();
        interview.setId(1);
        interview.setTitle("Some interview");
        interview.setAdditional("Some description");
        interview.setContactBy("Some contact");
        interview.setApproximateDate("30.02.2024");
        interview.setCreateDate("06.10.2023");
        interview.setTopicId(1);
        when(authService.userInfo(token)).thenReturn(userInfo);
        when(interviewService.create(token, interview)).thenReturn(interview);
        when(topicsService.getCategoryIdNameDTOByTopicId(1))
                .thenReturn(new CategoryIdNameDTO(1, "some category"));
        when(uriProvider.getUri(Mockito.anyString())).thenReturn("https://service");
        mockMvc.perform(post("/interview/create")
                        .flashAttr("interviewDTO", interview)
                        .sessionAttr("token", token)
                        .param("topicId", "1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/interview/" + interview.getId()));
    }

    @Test
    void whenPostCancelInterviewThenReturnInterviewsPage() throws Exception {
        InterviewDTO interviewDTO = new InterviewDTO();
        interviewDTO.setId(1);
        interviewDTO.setCancelBy("User");
        given(interviewService.getById(any(), anyInt())).willReturn(interviewDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/interview/cancel")
                        .flashAttr("interviewDTO", interviewDTO)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/interviews/"));
    }

    @Test
    void whenGetCancelInterviewPageThenReturnPageToCommentCanceling() throws Exception {
        var token = "123456";
        var interviewId = 999;
        var userInfo = new UserInfoDTO();
        var interviewDTO = new InterviewDTO();
        interviewDTO.setStatusId(StatusInterview.IS_NEW.getId());
        interviewDTO.setId(interviewId);
        when(authService.userInfo(token)).thenReturn(userInfo);
        when(interviewService.getById(token, interviewId)).thenReturn(interviewDTO);
        when(interviewService.isAuthor(userInfo, interviewDTO)).thenReturn(true);
        when(interviewService.getById(token, interviewDTO.getId())).thenReturn(interviewDTO);
        mockMvc.perform(get("/interview/cancel/reason/{interviewId}", interviewDTO.getId())
                        .sessionAttr("token", token))
                .andDo(print())
                .andExpect(view().name("interview/reasonOfCancel"));
    }

    @Test
    void whenGetCancelInterviewPageAndYouAreNotAuthor() throws Exception {
        var token = "123456";
        var interviewId = 999;
        var userInfo = new UserInfoDTO();
        var interviewDTO = new InterviewDTO();
        interviewDTO.setStatusId(StatusInterview.IS_NEW.getId());
        interviewDTO.setId(interviewId);
        when(authService.userInfo(token)).thenReturn(userInfo);
        when(interviewService.getById(token, interviewId)).thenReturn(interviewDTO);
        when(interviewService.isAuthor(userInfo, interviewDTO)).thenReturn(false);
        when(interviewService.getById(token, interviewDTO.getId())).thenReturn(interviewDTO);
        mockMvc.perform(get("/interview/cancel/reason/{interviewId}", interviewDTO.getId())
                        .sessionAttr("token", token))
                .andDo(print())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    void whenGetCancelInterviewPageAndInterviewStatusIsNotNewNeitherInProgress() throws Exception {
        var token = "123456";
        var interviewId = 999;
        var userInfo = new UserInfoDTO();
        var interviewDTO = new InterviewDTO();
        interviewDTO.setStatusId(StatusInterview.IS_FEEDBACK.getId());
        interviewDTO.setId(interviewId);
        when(authService.userInfo(token)).thenReturn(userInfo);
        when(interviewService.getById(token, interviewId)).thenReturn(interviewDTO);
        when(interviewService.isAuthor(userInfo, interviewDTO)).thenReturn(true);
        when(interviewService.getById(token, interviewDTO.getId())).thenReturn(interviewDTO);
        mockMvc.perform(get("/interview/cancel/reason/{interviewId}", interviewDTO.getId())
                        .sessionAttr("token", token))
                .andDo(print())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    void wenGetEditViewThenReturnEditView() throws Exception {
        var token = "123456";
        var userInfo = new UserInfoDTO();
        userInfo.setId(99);
        userInfo.setEmail("email");
        userInfo.setUsername("name");
        var interview = new InterviewDTO();
        interview.setId(1);
        interview.setTitle("title");
        interview.setSubmitterId(userInfo.getId());
        interview.setTopicId(1);
        var breadcrumbs = List.of(
                new Breadcrumb("Главная", "/index"),
                new Breadcrumb("Собеседования", "/interviews/"),
                new Breadcrumb(interview.getTitle(), String.format("/interview/edit/%d", interview.getId())));
        when(authService.userInfo(token)).thenReturn(userInfo);
        when(interviewService.getById(token, interview.getId())).thenReturn(interview);
        when(uriProvider.getUri(Mockito.anyString())).thenReturn("https://service");
        mockMvc.perform(get("/interview/edit/{id}", interview.getId())
                        .sessionAttr("token", token))
                .andDo(print())
                .andExpect(model().attribute("userInfo", userInfo))
                .andExpect(model().attribute("interview", interview))
                .andExpect(model().attribute("breadcrumbs", breadcrumbs))
                .andExpect(status().isOk())
                .andExpect(view().name("interview/interviewEdit"));
    }

    @Test
    void wenGetEditViewUserNotCreatedInterviewThenReturnRedirectInterviews() throws Exception {
        var token = "123456";
        var userInfo = new UserInfoDTO();
        userInfo.setId(99);
        userInfo.setEmail("email");
        userInfo.setUsername("name");
        var interview = new InterviewDTO();
        interview.setId(1);
        interview.setSubmitterId(22);
        interview.setTopicId(1);
        when(authService.userInfo(token)).thenReturn(userInfo);
        when(interviewService.getById(token, interview.getId())).thenReturn(interview);
        mockMvc.perform(get("/interview/edit/{id}", interview.getId())
                        .sessionAttr("token", token))
                .andDo(print())
                .andExpect(model().attribute("userInfo", userInfo))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/interview/" + interview.getId()));
    }

    @Test
    void wenGetEditViewUserThrowsExceptionThenReturnRedirectInterviews() throws Exception {
        var token = "123456";
        var userInfo = new UserInfoDTO();
        userInfo.setId(99);
        userInfo.setEmail("email");
        userInfo.setUsername("name");
        var interview = new InterviewDTO();
        interview.setId(1);
        interview.setSubmitterId(userInfo.getId());
        interview.setTopicId(1);
        when(authService.userInfo(token)).thenReturn(userInfo);
        when(interviewService.getById(token, interview.getId())).thenThrow(JsonProcessingException.class);
        mockMvc.perform(get("/interview/edit/{id}", interview.getId())
                        .sessionAttr("token", token))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/interviews/"));
    }


    @Test
    void whenPostUpdateInterviewThenRedirectUpdateInterview() throws Exception {
        var token = "123456";
        var userInfo = new UserInfoDTO();
        userInfo.setId(99);
        var interview = new InterviewDTO();
        interview.setId(1);
        interview.setSubmitterId(userInfo.getId());
        interview.setTopicId(1);
        mockMvc.perform(post("/interview/update")
                        .sessionAttr("token", token)
                        .param("id", String.valueOf(interview.getId()))
                        .param("submitterId", String.valueOf(interview.getSubmitterId()))
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/interview/" + interview.getId()));
    }

    @Test
    void whenPostUpdateInterviewThrowsThenRedirectEditInterview() throws Exception {
        var token = "123456";
        var userInfo = new UserInfoDTO();
        userInfo.setId(99);
        var interview = new InterviewDTO();
        interview.setId(1);
        interview.setSubmitterId(userInfo.getId());
        doThrow(JsonProcessingException.class).when(interviewService).update(token, interview);
        mockMvc.perform(post("/interview/update")
                        .sessionAttr("token", token)
                        .param("id", String.valueOf(interview.getId()))
                        .param("submitterId", String.valueOf(interview.getSubmitterId()))
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/interview/edit/" + interview.getId()));
    }

    @Test
    void whenParticipateSuccessful() throws Exception {
        var token = "123456";
        var userInfo = new UserInfoDTO();
        var userId = 99;
        userInfo.setId(userId);
        var interviewId = 7;
        var interview = new InterviewDTO();
        interview.setId(interviewId);
        interview.setSubmitterId(14375842);
        interview.setTitle("Some title");
        var wishers = IntStream.range(1, 3).mapToObj(i -> {
            var wisher = new WisherDto();
            wisher.setId(i);
            wisher.setInterviewId(interviewId);
            wisher.setUserId(userId + i);
            wisher.setContactBy(String.format("user_%d@mail.cd", i));
            wisher.setApprove(i % 2 == 0);
            return wisher;
        }).toList();
        var interviewStatistics = new HashMap<Integer, InterviewStatistic>();
        IntStream.range(1, 3).forEach(i ->
                interviewStatistics.put(i, new InterviewStatistic(i + 1, i, i - 1)));
        when(interviewService.getById(token, interviewId)).thenReturn(interview);
        when(wisherService.getAllWisherDtoByInterviewId(token, String.valueOf(interviewId)))
                .thenReturn(wishers);
        when(wisherService.getInterviewStatistic(wishers)).thenReturn(interviewStatistics);
        when(authService.userInfo(token)).thenReturn(userInfo);
        when(uriProvider.getUri(Mockito.anyString())).thenReturn("https://service");
        mockMvc.perform(get(String.format("/interview/%d/participate", interviewId))
                        .sessionAttr("token", token)
                )
                .andDo(print()).andExpectAll(status().isOk(),
                        model().attribute("breadcrumbs", List.of(
                                new Breadcrumb("Главная", "/index"),
                                new Breadcrumb("Собеседования", "/interviews/"),
                                new Breadcrumb(interview.getTitle(),
                                        String.format("/interview/%d", interviewId)),
                                new Breadcrumb("принять участие в собеседовании",
                                        "/participate"))),
                        view().name("interview/participate"));
    }

    @Test
    void whenTryToParticipateWithSubmitterThenRedirectToInterviewPage() throws Exception {
        var token = "123456";
        var userInfo = new UserInfoDTO();
        var userId = 99;
        userInfo.setId(userId);
        var interviewId = 7;
        var interview = new InterviewDTO();
        interview.setId(interviewId);
        interview.setSubmitterId(userInfo.getId());
        when(interviewService.getById(token, interviewId)).thenReturn(interview);
        when(authService.userInfo(token)).thenReturn(userInfo);
        when(uriProvider.getUri(Mockito.anyString())).thenReturn("https://service");
        mockMvc.perform(get(String.format("/interview/%d/participate", interviewId))
                        .sessionAttr("token", token))
                .andDo(print()).andExpectAll(status().is3xxRedirection(),
                        view().name(String.format("redirect:/interview/%d", interviewId)));
    }

    @Test
    void whenGetGetVacancyLinkPageThenReturnPagePostVacancylink() throws Exception {
        var token = "123456";
        mockMvc.perform(get("/interview/getVacancyLink")
                        .sessionAttr("token", token))
                .andDo(print())
                .andExpect(view().name("interview/getVacancyLink"));
    }
}