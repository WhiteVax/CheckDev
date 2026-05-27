package ru.checkdev.mock.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.checkdev.mock.domain.Wisher;
import ru.checkdev.mock.dto.InterviewDTO;
import ru.checkdev.mock.dto.WisherDto;
import ru.checkdev.mock.mapper.InterviewMapper;
import ru.checkdev.mock.mapper.WisherMapper;
import ru.checkdev.mock.service.InterviewService;
import ru.checkdev.mock.service.WisherService;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.Optional;

@Tag(name = "WisherController", description = "Wisher REST API")
@RestController
@RequestMapping("/wisher")
@AllArgsConstructor
public class WisherController {

    private final InterviewService interviewService;
    private final WisherService wisherService;

    @PostMapping("/")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Wisher> save(@Valid @RequestBody WisherDto wisherDto) throws SQLException {
        Optional<InterviewDTO> interviewDtoOptional = interviewService.findById(wisherDto.getInterviewId());
        if (interviewDtoOptional.isEmpty()) {
            throw new SQLException("This interview is missing");
        }
        Optional<Wisher> rsl = wisherService.save(
                new WisherMapper().getWisher(wisherDto, interviewDtoOptional.get()));
        if (rsl.isEmpty()) {
            throw new SQLException("An error occurred while saving data");
        }
        return rsl
                .map(wisher -> new ResponseEntity<>(wisher, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Wisher> getById(@Valid @PathVariable int id) throws SQLException {
        Optional<Wisher> rsl = wisherService.findById(id);
        if (rsl.isEmpty()) {
            throw new SQLException("There is no wisher with this number");
        }
        return rsl
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Wisher> update(@Valid @RequestBody WisherDto wisherDto) throws SQLException {
        Optional<Wisher> optionalWisher = wisherService.findById(wisherDto.getId());
        if (optionalWisher.isEmpty()) {
            throw new SQLException("There is no wisher with this number");
        }
        Wisher rsl = optionalWisher.get();
        Optional<InterviewDTO> optionalDtoInterview = interviewService.findById(wisherDto.getInterviewId());
        if (optionalDtoInterview.isEmpty()) {
            throw new SQLException("There is no interview with this number");
        }
        var interview = InterviewMapper.getInterview(optionalDtoInterview.get());
        rsl.setInterview(interview);
        rsl.setUserId(wisherDto.getUserId());
        rsl.setContactBy(wisherDto.getContactBy());
        rsl.setApprove(wisherDto.isApprove());
        return ResponseEntity.status(wisherService.update(rsl) ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .body(rsl);
    }
}
