package ru.job4j.site.controller.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.site.SiteSrv;
import ru.job4j.site.service.MessageService;
import ru.job4j.site.service.EurekaUriProvider;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SiteSrv.class)
@AutoConfigureMockMvc
public class MessagesRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private EurekaUriProvider uriProvider;

    @Test
    public void testDeleteMessage() throws Exception {
        int messageId = 1;
        mockMvc.perform(delete("/messages_rest/delete/{messageId}", messageId))
                .andExpect(status().isOk());
        verify(messageService, times(1)).delete(null, messageId);
    }
}