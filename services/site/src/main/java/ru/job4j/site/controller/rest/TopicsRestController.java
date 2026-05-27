package ru.job4j.site.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.site.dto.TopicDTO;
import ru.job4j.site.service.TopicsService;

import java.util.List;

@Tag(name = "TopicsRestController", description = "Topics REST API")
@RestController
@RequestMapping("/topics_rest")
@AllArgsConstructor
public class TopicsRestController {

    private final TopicsService topicsService;

    @GetMapping("/{categoryId}")
    public ResponseEntity<List<TopicDTO>> getTopics(@PathVariable int categoryId)
            throws JsonProcessingException {
        return new ResponseEntity<>(topicsService.getByCategory(categoryId), HttpStatus.OK);
    }
}