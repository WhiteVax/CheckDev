package ru.checkdev.mock.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import ru.checkdev.mock.domain.Interview;
import ru.checkdev.mock.dto.FilterRequestParams;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class InterviewFilterSpecificationsTest {

    @InjectMocks
    private InterviewFilterSpecifications interviewFilterSpecifications;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Root<Interview> root;

    @Mock
    private CriteriaQuery<Interview> query;

    @Test
    public void testCreateSpecifications() {
        var filterRequestParams = new FilterRequestParams(
                List.of(1), 1, 1, 1, 1, 0, false);
        Specification<Interview> specification =
                interviewFilterSpecifications.createSpecifications(filterRequestParams);
        assertNotNull(specification);
    }
}