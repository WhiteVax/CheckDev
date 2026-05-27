package ru.checkdev.mock.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.checkdev.mock.domain.Filter;
import ru.checkdev.mock.domain.FilterProfile;
import ru.checkdev.mock.enums.FilterProfileStore;
import ru.checkdev.mock.service.FilterService;

import java.sql.SQLException;
import java.util.List;

@Tag(name = "FilterController", description = "Filter REST API")
@RestController
@RequestMapping("/filter")
@AllArgsConstructor
public class FilterController {

    private final FilterService filterService;

    @PostMapping("/")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Filter> save(@RequestBody Filter filter) throws SQLException {
        return new ResponseEntity<>(
                filterService
                        .save(filter)
                        .orElseThrow(() -> new SQLException("An error occurred while saving data")),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Filter> getByUserId(@PathVariable int userId) {
        return filterService.findByUserId(userId).map(
                value -> new ResponseEntity<>(value, HttpStatus.OK)
        ).orElseGet(() -> new ResponseEntity<>(new Filter(), HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> deleteByUserId(@PathVariable int userId) {
        int result = filterService.deleteByUserId(userId);
        if (result > 0) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/profiles")
    public ResponseEntity<List<FilterProfile>> getProfiles() {
        return new ResponseEntity<>(FilterProfileStore.getFilterProfiles(), HttpStatus.OK);
    }
}
