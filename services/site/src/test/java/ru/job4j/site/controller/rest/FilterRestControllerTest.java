package ru.job4j.site.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.site.SiteSrv;
import ru.job4j.site.dto.FilterDTO;
import ru.job4j.site.service.FilterService;
import ru.job4j.site.service.EurekaUriProvider;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SiteSrv.class)
@AutoConfigureMockMvc
public class FilterRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FilterService filterService;

    @MockBean
    private EurekaUriProvider uriProvider;

    @Test
    public void whenGetByUserId() throws Exception {
        int userId = 1;
        String token = "123";
        var filter = new FilterDTO(1, 1, 0, 0, 0, 0);
        when(filterService.getByUserId(token, userId)).thenReturn(filter);
        mockMvc.perform(get("/filter/1")
                        .sessionAttr("token", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(filter)));
    }

    @Test
    public void whenSave() throws Exception {
        String token = "123";
        var filter = new FilterDTO(1, 1, 0, 0, 0, 0);
        when(filterService.save(token, filter)).thenReturn(filter);
        mockMvc.perform(post("/filter/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk());
    }

    @Test
    public void whenDeleteByUserId() throws Exception {
        mockMvc.perform(delete("/filter/delete/1")).andExpect(status().isOk());
    }
}