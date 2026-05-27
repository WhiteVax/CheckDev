package ru.checkdev.mock.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.checkdev.mock.domain.Filter;
import ru.checkdev.mock.repository.FilterRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class FilterService {

    private final FilterRepository filterRepository;

    public Optional<Filter> save(Filter filter) {
        return Optional.of(filterRepository.save(filter));
    }

    public Optional<Filter> findByUserId(int userId) {
        var filter = filterRepository.getByUserId(userId);
        return filter == null ? Optional.empty() : Optional.of(filter);
    }

    public int deleteByUserId(int userId) {
        return filterRepository.deleteByUserId(userId);
    }
}