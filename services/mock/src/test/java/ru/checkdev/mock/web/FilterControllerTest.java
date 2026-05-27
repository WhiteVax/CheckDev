package ru.checkdev.mock.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.checkdev.mock.MockSrv;
import ru.checkdev.mock.domain.Filter;
import ru.checkdev.mock.domain.FilterProfile;
import ru.checkdev.mock.enums.FilterProfileStore;
import ru.checkdev.mock.service.FilterService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MockSrv.class)
@AutoConfigureMockMvc
class FilterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilterService filterService;

    @Disabled
    @Test
    @WithMockUser
    public void whenFilterSaved() throws Exception {
        var filter = new Filter(1, 1, 1, 0, 0, 0);
        when(filterService.save(filter)).thenReturn(Optional.of(filter));
        String json = new GsonBuilder().serializeNulls().create().toJson(filter);
        mockMvc.perform(post("/filter/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filter)))
                .andDo(print()).andExpectAll(status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().string(json));
    }

    @Test
    public void whenFilterSavedIsUnauthorized() throws Exception {
        var filter = new Filter(1, 1, 1, 0, 0, 0);
        when(filterService.save(filter)).thenReturn(Optional.of(filter));
        String json = new GsonBuilder().serializeNulls().create().toJson(filter);
        mockMvc.perform(post("/filter/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filter)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenFilterFindByUserId() throws Exception {
        var filter = new Filter(1, 1, 1, 0, 0, 0);
        when(filterService.findByUserId(1)).thenReturn(Optional.of(filter));
        String json = new GsonBuilder().serializeNulls().create().toJson(filter);
        mockMvc.perform(get("/filter/1"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().string(json));
    }

    @Test
    public void whenFilterNotFindByUserId() throws Exception {
        var filter = new Filter();
        when(filterService.findByUserId(1)).thenReturn(Optional.empty());
        String json = new GsonBuilder().serializeNulls().create().toJson(filter);
        mockMvc.perform(get("/filter/1"))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().string(json));
    }

    @Disabled
    @Test
    @WithMockUser
    public void whenFilterDeleted() throws Exception {
        var filter = new Filter(1, 1, 1, 0, 0, 0);
        when(filterService.deleteByUserId(1)).thenReturn(1);
        mockMvc.perform(delete("/filter/delete/1"))
                .andDo(print())
                .andExpectAll(status().isOk(),
                        content().string("true"));
    }

    @Test
    public void whenFilterDeletedIsUnauthorized() throws Exception {
        var filter = new Filter(1, 1, 1, 0, 0, 0);
        when(filterService.deleteByUserId(1)).thenReturn(1);
        mockMvc.perform(delete("/filter/delete/1"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Disabled
    @Test
    @WithMockUser
    public void whenFilterCanNotBeDeleted() throws Exception {
        when(filterService.deleteByUserId(1)).thenReturn(0);
        mockMvc.perform(delete("/filter/delete/1"))
                .andDo(print())
                .andExpectAll(status().isNotFound(),
                        content().string("false"));
    }

    @Test
    void whenGetFilterProfiles() throws Exception {
        List<FilterProfile> profiles = FilterProfileStore.getFilterProfiles();
        String expectedJson = new GsonBuilder().serializeNulls().create().toJson(profiles);
        byte[] utf8Json = expectedJson.getBytes(StandardCharsets.UTF_8);
        mockMvc.perform(get("/filter/profiles"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().bytes(utf8Json),
                        content().json(expectedJson));
    }
}
