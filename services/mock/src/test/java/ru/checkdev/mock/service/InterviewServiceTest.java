package ru.checkdev.mock.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;
import ru.checkdev.mock.domain.Interview;
import ru.checkdev.mock.dto.FilterRequestParams;
import ru.checkdev.mock.dto.InterviewDTO;
import ru.checkdev.mock.enums.StatusInterview;
import ru.checkdev.mock.mapper.InterviewMapper;
import ru.checkdev.mock.repository.InterviewRepository;
import ru.checkdev.mock.repository.WisherRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = InterviewService.class)
@RunWith(SpringRunner.class)
class InterviewServiceTest {

    @MockBean
    private InterviewRepository interviewRepository;

    @MockBean
    private WisherRepository wisherRepository;

    @MockBean
    private InterviewFilterSpecifications interviewFilterSpecifications;

    @Autowired
    private InterviewService interviewService;

    private Interview interview = Interview.of()
            .id(1)
            .mode(2)
            .status(StatusInterview.IS_NEW)
            .submitterId(3)
            .title("test_title")
            .additional("test_additional")
            .contactBy("test_contact_by")
            .approximateDate("test_approximate_date")
            .createDate(
                    Timestamp
                            .valueOf(
                                    LocalDateTime.now()
                                            .truncatedTo(ChronoUnit.MINUTES)))
            .topicId(1)
            .build();

    @Test
    public void whenSaveAndGetTheSame() {
        when(interviewRepository.save(any(Interview.class))).thenReturn(interview);
        var interviewDTO = InterviewMapper.getInterviewDTO(interview);
        Optional<InterviewDTO> actual = interviewService.save(interviewDTO);
        assertThat(actual, is(Optional.of(interviewDTO)));
    }

    @Test
    public void whenSaveAndGetEmpty() {
        when(interviewRepository.save(any(Interview.class))).thenThrow(new DataIntegrityViolationException(""));
        var interviewDTO = InterviewMapper.getInterviewDTO(interview);
        Optional<InterviewDTO> actual = interviewService.save(interviewDTO);
        assertThat(actual, is(Optional.empty()));
    }

    @Test
    public void whenGetAll() {
        List<Interview> interviews = IntStream.range(0, 5).mapToObj(i -> {
            var interview = new Interview();
            interview.setId(i);
            interview.setMode(1);
            interview.setStatus(StatusInterview.IS_NEW);
            interview.setSubmitterId(1);
            interview.setTitle(String.format("Interview_%d", i));
            interview.setAdditional("Some text");
            interview.setContactBy("Some contact");
            interview.setApproximateDate("30.02.2024");
            interview.setTopicId(1);
            interview.setCreateDate(new Timestamp(System.currentTimeMillis()));
            return interview;
        }).toList();
        var page = new PageImpl<>(interviews);
        var pageDto = page.map(InterviewMapper::getInterviewDTO);
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createDate"));
        when(interviewRepository.findAll(pageable)).thenReturn(page);
        var actual = interviewService.findPaging(0, 5);
        assertThat(actual, is(pageDto));
    }

    @Test
    public void whenGetAllByUserIdRelated() {
        int userId = 1;
        var status = StatusInterview.IS_NEW;
        List<Interview> interviews = IntStream.range(0, 5).mapToObj(i -> {
            var interview = new Interview();
            interview.setId(i);
            interview.setMode(1);
            interview.setStatus(StatusInterview.IS_NEW);
            interview.setSubmitterId(1);
            interview.setTitle(String.format("Interview_%d", i));
            interview.setAdditional("Some text");
            interview.setContactBy("Some contact");
            interview.setApproximateDate("30.02.2024");
            interview.setTopicId(1);
            interview.setCreateDate(new Timestamp(System.currentTimeMillis()));
            return interview;
        }).toList();
        var page = new PageImpl<>(interviews);
        var pageDto = page.map(InterviewMapper::getInterviewDTO);
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createDate"));
        when(wisherRepository.findInterviewByUserIdApproved(userId, Pageable.unpaged())).thenReturn(Page.empty());
        when(interviewRepository.findAllByUserIdRelated(userId, status, List.of(), pageable)).thenReturn(page);
        var actual = interviewService.findPagingByUserIdRelated(0, 5, userId);
        assertThat(actual, is(pageDto));
        assertThat(actual.getTotalElements(), is(5L));
    }

    @Test
    public void whenGetAllByUserIdRelatedAndNothingFound() {
        int userId = 1;
        var status = StatusInterview.IS_NEW;
        Page<Interview> page = Page.empty();
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createDate"));
        when(wisherRepository.findInterviewByUserIdApproved(userId, Pageable.unpaged())).thenReturn(Page.empty());
        when(interviewRepository.findAllByUserIdRelated(userId, status, List.of(), pageable)).thenReturn(page);
        var actual = interviewService.findPagingByUserIdRelated(0, 5, userId);
        assertThat(actual, is(page));
        assertThat(actual.getTotalElements(), is(0L));
    }

    @Test
    public void whenFindByIdIsCorrect() {
        when(interviewRepository.findById(any(Integer.class))).thenReturn(Optional.of(interview));
        var expect = InterviewMapper.getInterviewDTO(interview);
        var actual = interviewService.findById(1);
        assertThat(actual).isNotEmpty();
        assertThat(actual.get(), is(expect));
    }

    @Test
    public void whenFindByIdIsNotCorrect() {
        when(interviewRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        var actual = interviewService.findById(1);
        assertThat(actual, is(Optional.empty()));
    }

    @Test
    public void whenUpdateIsCorrect() {
        when(interviewRepository.save(any(Interview.class))).thenReturn(interview);
        var interviewDTO = InterviewMapper.getInterviewDTO(interview);
        Optional<InterviewDTO> actual = interviewService.save(interviewDTO);
        assertThat(actual).isNotEmpty();
        assertThat(actual.get(), is(interviewDTO));
    }

    @Test
    public void whenDeleteIsCorrect() {
        doNothing().when(interviewRepository).deleteById(interview.getId());
        interviewService.delete(interview.getId());
        assertThat(true).isTrue();
    }

    @Test
    public void whenGetAllWithTopicIdIsNull() {
        InterviewDTO interviewDTO = InterviewMapper.getInterviewDTO(interview);
        var interviewList = List.of(interview);
        var expectList = List.of(interviewDTO);
        when(interviewRepository.findAll()).thenReturn(interviewList);
        var actual = interviewService.findAll();
        assertThat(actual, is(expectList));
    }

    @Test
    public void whenFindByTypeAllWithTopicIdIsNull() {
        Interview interviewWithTopicId = interview;
        interviewWithTopicId.setTopicId(1);
        interviewWithTopicId.setMode(1);
        var expectList = List.of(InterviewMapper.getInterviewDTO(interviewWithTopicId));
        when(interviewRepository.findByMode(1)).thenReturn(List.of(interviewWithTopicId));
        var actual = interviewService.findByMode(1);
        assertThat(actual, is(expectList));
    }

    @Test
    public void whenUpdateStatusThenTrue() {
        var interviewDTO = InterviewMapper.getInterviewDTO(interview);
        doNothing().when(interviewRepository).updateStatus(1, StatusInterview.IN_PROGRESS);
        var actual = interviewService.updateStatus(interviewDTO);
        assertThat(actual).isTrue();
    }

    @Test
    void whenFindAllIdByNoFeedbackThenReturnListInterview() {
        int submitterId = 1;
        int wisherUser = 2;
        Interview interview = Interview.of()
                .id(1)
                .submitterId(submitterId)
                .createDate(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)))
                .topicId(1)
                .build();
        List<Interview> listInterview = List.of(interview);
        List<InterviewDTO> expected = List.of(InterviewMapper.getInterviewDTO(interview));
        doReturn(listInterview).when(interviewRepository).findAllByUserIdWisherIsApproveAndNoFeedback(wisherUser);
        List<InterviewDTO> actual = interviewService.findAllIdByNoFeedback(wisherUser);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void whenFindAllIdByNoFeedbackThenReturnEmptyList() {
        int wisherUser = 2;
        doReturn(Collections.emptyList()).when(interviewRepository).findAllByUserIdWisherIsApproveAndNoFeedback(wisherUser);
        List<InterviewDTO> actual = interviewService.findAllIdByNoFeedback(wisherUser);
        assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    public void whenGetAllWithStatusNew() {
        Interview interviewNewStatus = interview;
        var status = StatusInterview.IS_NEW;
        when(interviewRepository.findAllByStatus(status)).thenReturn(List.of(interviewNewStatus));
    }

    @Test
    public void whenPutNotNewStatusGetEmptyList() {
        var status = StatusInterview.IS_NEW;
        when(interviewRepository.findAllByStatus(status)).thenReturn(List.of());
        List<InterviewDTO> actual = interviewService.findNewInterview();
        assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    void whenGetAllWithSpecifications() {
        var filterRequestParams = new FilterRequestParams(
                List.of(1), 3, 0, 0, 1, 0, false);
        var specifications =
                interviewFilterSpecifications.createSpecifications(filterRequestParams);
        var page = new PageImpl<>(List.of(interview));
        when(interviewRepository.findAll(specifications, PageRequest.of(0, 1)))
                .thenReturn(page);
        assertThat(interviewService
                .getInterviewsWithFilters(0, 1, filterRequestParams))
                .isEqualTo(page.map(InterviewMapper::getInterviewDTO));
    }
}