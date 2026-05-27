package ru.job4j.site.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.job4j.site.service.MessageService;

import javax.servlet.http.HttpServletRequest;

import static ru.job4j.site.util.RequestResponseTools.getToken;

@Tag(name = "MessagesRestController", description = "Messages REST API")
@RestController
@AllArgsConstructor
@RequestMapping("/messages_rest")
public class MessagesRestController {

    private final MessageService messageService;

    @DeleteMapping("/delete/{messageId}")
    public void delete(@PathVariable int messageId, HttpServletRequest request)
            throws JsonProcessingException {
        messageService.delete(getToken(request), messageId);
    }

    @PutMapping("/setRead/{messageId}")
    public void update(@PathVariable int messageId, HttpServletRequest request)
            throws JsonProcessingException {
        messageService.setRead(getToken(request), messageId);
    }

    @PutMapping("/setReadAll/{userId}")
    public void setReadAll(@PathVariable int userId, HttpServletRequest request) {
        messageService.setReadAll(getToken(request), userId);
    }
}
