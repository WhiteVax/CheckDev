package ru.checkdev.mock.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.checkdev.mock.dto.InterviewDTO;
import ru.checkdev.mock.service.InterviewService;

import javax.validation.Valid;
import java.sql.SQLException;

@Tag(name = "InterviewController", description = "Interview REST API")
@RestController
@RequestMapping("/interview")
@AllArgsConstructor
public class InterviewController {
    private final InterviewService interviewService;

    @PostMapping("/")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InterviewDTO> save(@Valid @RequestBody InterviewDTO interviewDTO) throws SQLException {
        return new ResponseEntity<>(
                interviewService
                        .save(interviewDTO)
                        .orElseThrow(() -> new SQLException("An error occurred while saving data")),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterviewDTO> getById(@Valid @PathVariable int id) {
        return interviewService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InterviewDTO> update(@Valid @RequestBody InterviewDTO interviewDTO) {
        return ResponseEntity
                .status(interviewService.update(interviewDTO) ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .body(interviewDTO);
    }

    @PutMapping("/status/")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<HttpStatus> updateStatusInterview(@RequestBody InterviewDTO interviewDTO) {
        var result = interviewService.updateStatus(interviewDTO);
        return ResponseEntity.status(result ? HttpStatus.OK : HttpStatus.NOT_FOUND).build();
    }
}
