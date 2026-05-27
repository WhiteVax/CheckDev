package ru.job4j.site.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.site.dto.ProfileDTO;
import ru.job4j.site.dto.ProfileWithApprovedInterviewsDTO;
import ru.job4j.site.dto.UserInfoDTO;
import ru.job4j.site.dto.UsersApprovedInterviewsDTO;
import ru.job4j.site.service.AuthService;
import ru.job4j.site.service.ProfilesService;
import ru.job4j.site.service.WisherService;
import ru.job4j.site.service.EurekaUriProvider;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CheckDev пробное собеседование
 * ProfilesControllerTest тесты на контроллер IndexController
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 25.09.2023
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProfilesControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProfilesService profilesService;
    @MockBean
    private WisherService wisherService;
    @MockBean
    private AuthService authService;
    @MockBean
    private EurekaUriProvider uriProvider;

    @Test
    void whenGetProfileByIdThenReturnPageProfileView() throws Exception {
        var token = "123";
        var id = 1;
        var profile = new ProfileDTO(id, "username", "experience", 1,
                Calendar.getInstance(), Calendar.getInstance());
        var userInfo = new UserInfoDTO();
        userInfo.setId(1);
        var usersApprovedInterviewsDTO = new UsersApprovedInterviewsDTO(profile.getId(), 99);
        when(profilesService.getProfileById(id)).thenReturn(Optional.of(profile));
        when(authService.userInfo(token)).thenReturn(userInfo);
        when(wisherService.getUserIdWithCountedApprovedInterviews(token, String.valueOf(profile.getId())))
                .thenReturn(usersApprovedInterviewsDTO);
        when(uriProvider.getUri(Mockito.anyString())).thenReturn("https://service");
        this.mockMvc.perform(get("/profiles/{id}", profile.getId())
                        .sessionAttr("token", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("profile", profile))
                .andExpect(model().attribute("approvedInterviews",
                        usersApprovedInterviewsDTO.getApprovedInterviews()))
                .andExpect(view().name("profiles/profileView"));
    }

    @Test
    void whenGetAllProfilesThenReturnPageProfilesWithCountOfApprovedInterviews() throws Exception {
        var profile1 = new ProfileDTO(1, "username1", "experience1", 1, Calendar.getInstance(), Calendar.getInstance());
        var profile2 = new ProfileDTO(2, "username2", "experience2", 2, Calendar.getInstance(), Calendar.getInstance());
        var p1 = new ProfileWithApprovedInterviewsDTO(profile1, 5);
        var p2 = new ProfileWithApprovedInterviewsDTO(profile2, 10);
        var listProfile = List.of(p1, p2);
        when(profilesService.getAllProfilesWithApprovedInterviews(wisherService.getUsersIdWithCountedApprovedInterviews("token"))).thenReturn(listProfile);
        this.mockMvc.perform(get("/profiles/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("profiles", listProfile))
                .andExpect(view().name("profiles/profiles"));
    }
}