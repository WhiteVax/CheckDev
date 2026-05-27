package ru.checkdev.mock.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;
import ru.checkdev.mock.domain.Interview;
import ru.checkdev.mock.domain.Wisher;
import ru.checkdev.mock.dto.UsersApprovedInterviewsDTO;
import ru.checkdev.mock.dto.WisherDto;
import ru.checkdev.mock.repository.WisherRepository;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WisherService.class)
class WisherServiceTest {

    @MockBean
    private WisherRepository wisherRepository;

    @Autowired
    private WisherService wisherService;

    private Interview interview = Interview.of()
            .id(1)
            .mode(2)
            .submitterId(3)
            .title("test_title")
            .additional("test_additional")
            .contactBy("test_contact_by")
            .approximateDate("test_approximate_date")
            .createDate(new Timestamp(System.currentTimeMillis()))
            .build();

    private Wisher wisher = Wisher.of()
            .id(1)
            .interview(interview)
            .userId(1)
            .contactBy("test_contact_by")
            .approve(true)
            .build();

    @Test
    public void whenSaveAndGetTheSame() {
        when(wisherRepository.save(any(Wisher.class))).thenReturn(wisher);
        var actual = wisherService.save(wisher);
        assertThat(actual, is(Optional.of(wisher)));
    }

    @Test
    public void whenSaveAndGetEmpty() {
        when(wisherRepository.save(any(Wisher.class))).thenThrow(new DataIntegrityViolationException(""));
        var actual = wisherService.save(wisher);
        assertThat(actual, is(Optional.empty()));
    }

    @Test
    public void whenGetAll() {
        when(wisherRepository.findAll()).thenReturn(List.of(wisher));
        var actual = wisherService.findAll();
        assertThat(actual, is(List.of(wisher)));
    }

    @Test
    public void whenFindByIdIsCorrect() {
        when(wisherRepository.findById(any(Integer.class))).thenReturn(Optional.of(wisher));
        var actual = wisherService.findById(1);
        assertThat(actual, is(Optional.of(wisher)));
    }

    @Test
    public void whenFindByIdIsNotCorrect() {
        when(wisherRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        var actual = wisherService.findById(1);
        assertThat(actual, is(Optional.empty()));
    }

    @Test
    public void whenUpdateIsCorrect() {
        when(wisherRepository.save(any(Wisher.class))).thenReturn(wisher);
        var actual = wisherService.save(wisher);
        assertThat(actual, is(Optional.of(wisher)));
    }

    @Test
    public void whenDeleteIsCorrect() {
        when(wisherRepository.findById(any(Integer.class))).thenReturn(Optional.of(wisher));
        var actual = wisherService.delete(wisher);
        assertThat(actual, is(true));
    }

    @Test
    public void whenDeleteIsNotCorrect() {
        when(wisherRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        var actual = wisherService.delete(wisher);
        assertThat(actual, is(false));
    }

    @Test
    void whenFindByInterviewThenReturnListWisher() {
        var interview = Interview.of().id(1).build();
        var wisher = Wisher.of().id(1).interview(interview).build();
        var wishers = List.of(wisher);
        when(wisherRepository.findByInterview(interview)).thenReturn(wishers);
        var actual = wisherService.findByInterview(interview);
        assertThat(actual, is(wishers));
    }

    @Test
    void whenFindByInterviewThenEmpty() {
        var interview = Interview.of().id(1).build();
        when(wisherRepository.findByInterview(interview)).thenReturn(Collections.emptyList());
        var actual = wisherService.findByInterview(interview);
        assertThat(actual.isEmpty(), is(true));
    }

    @Test
    void whenUpdateWisherThenTrue() {
        var wisher = new Wisher();
        when(wisherRepository.save(wisher)).thenReturn(wisher);
        var actual = wisherService.update(wisher);
        assertThat(actual, is(true));
    }

    @Test
    void whenFindAllWisherDTOThenReturnDtoList() {
        var wisher = new WisherDto();
        var expectList = List.of(wisher);
        when(wisherRepository.findAllWiserDto()).thenReturn(expectList);
        var actual = wisherService.findAllWisherDto();
        assertThat(actual, is(expectList));
    }

    @Test
    void whenFindAllWisherDTOThenEmptyList() {
        when(wisherRepository.findAllWiserDto()).thenReturn(Collections.emptyList());
        var actual = wisherService.findAllWisherDto();
        assertThat(actual.isEmpty(), is(true));
    }

    @Test
    void whenFindWisherDtoByInterviewIdThenReturnListWisherDTO() {
        var interview = Interview.of().id(1).build();
        var wisher = new WisherDto();
        wisher.setInterviewId(interview.getId());
        var wishers = List.of(wisher);
        when(wisherRepository.findWisherDTOByInterviewId(interview.getId())).thenReturn(wishers);
        var actual = wisherService.findWisherDtoByInterviewId(interview.getId());
        assertThat(actual, is(wishers));
    }

    @Test
    void whenGetUserIdWithCountedApprovedInterviewsAndUserExistsThenGetDtoWithCountedInterviews() {
        var dto = new UsersApprovedInterviewsDTO(1, 2);
        when(wisherRepository.getUserIdWithCountedApprovedInterviews(dto.getUserId()))
                .thenReturn(Optional.of(dto));
        var actual = wisherService.getUserIdWithCountedApprovedInterviews(dto.getUserId());
        assertThat(actual, is(dto));
    }

    @Test
    void whenGetUserIdWithCountedApprovedInterviewsAndUserNotExistsThenGetDtoWithCountZero() {
        var dto = new UsersApprovedInterviewsDTO(1, 0);
        when(wisherRepository.getUserIdWithCountedApprovedInterviews(dto.getUserId()))
                .thenReturn(Optional.empty());
        var actual = wisherService.getUserIdWithCountedApprovedInterviews(dto.getUserId());
        assertThat(actual, is(dto));
    }

}