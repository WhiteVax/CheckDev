package ru.checkdev.mock.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.checkdev.mock.dto.FilterRequestParams;
import ru.checkdev.mock.dto.InterviewDTO;
import ru.checkdev.mock.service.InterviewService;

import java.sql.SQLException;
import java.util.List;

@Tag(name = "InterviewsController", description = "Interviews REST API")
@RestController
@RequestMapping("/interviews")
@AllArgsConstructor
public class InterviewsController {

    private final InterviewService interviewService;

    /*Аннотация не работает
    @PreAuthorize("isAuthenticated()") */
    @GetMapping("/")
    public ResponseEntity<Page<InterviewDTO>> findAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size
    ) throws SQLException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(interviewService.findPaging(page, size));
    }

    @GetMapping("/last")
    public ResponseEntity<List<InterviewDTO>> findLastThree() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(interviewService.findLast());
    }

    @GetMapping("/{mode}")
    public ResponseEntity<List<InterviewDTO>> findByMode(@PathVariable int mode) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(interviewService.findByMode(mode));
    }

    @GetMapping("/findByUserIdRelated/{userId}")
    public ResponseEntity<Page<InterviewDTO>> findByUserIdRelated(
            @PathVariable int userId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(interviewService.findPagingByUserIdRelated(page, size, userId));
    }

    @GetMapping("/noFeedback/{uId}")
    public ResponseEntity<List<InterviewDTO>> getAllNoFeedback(@PathVariable("uId") int uId) {
        List<InterviewDTO> interviews = interviewService.findAllIdByNoFeedback(uId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(interviews);
    }

    @GetMapping("/interviewStatusNew")
    public ResponseEntity<List<InterviewDTO>> getAllNewInterview() {
        List<InterviewDTO> interviews = interviewService.findNewInterview();
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(interviews);
    }

    @GetMapping("/getInterviews")
    public ResponseEntity<Page<InterviewDTO>> getInterviews(
            @RequestHeader("filter-request-params") String json,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) throws JsonProcessingException {
        FilterRequestParams filterRequestParams = new ObjectMapper().readValue(json, FilterRequestParams.class);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(interviewService.getInterviewsWithFilters(page, size, filterRequestParams));
    }
}
