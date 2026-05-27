package ru.checkdev.mock.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final WisherRepository wisherRepository;
    private final InterviewFilterSpecifications interviewFilterSpecifications;

    private static final Logger LOG = LoggerFactory.getLogger(InterviewService.class.getName());

    public Optional<InterviewDTO> save(InterviewDTO interviewDTO) {
        Optional<InterviewDTO> rsl = Optional.empty();
        var interview = InterviewMapper.getInterview(interviewDTO);
        interview.setCreateDate(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)));
        try {
            var saveInterview = interviewRepository.save(interview);
            rsl = Optional.of(InterviewMapper.getInterviewDTO(saveInterview));
        } catch (DataIntegrityViolationException e) {
            LOG.error("Error!", e);
        }
        return rsl;
    }

    public List<InterviewDTO> findAll() {
        return interviewRepository.findAll().stream()
                .peek(interview -> {
                    if (interview.getTopicId() == null) {
                        interview.setTopicId(1);
                    }
                })
                .map(InterviewMapper::getInterviewDTO)
                .collect(Collectors.toList());
    }

    /**
     * Получаем последние 5 собеседований.
     *
     * @return List<InterviewDTO>
     */
    public List<InterviewDTO> findLast() {
        var status = StatusInterview.IS_NEW;
        Pageable topFive = PageRequest.of(0, 5);
        return interviewRepository.findAllByStatusOrderByCreateDateDesc(status, topFive)
                .stream()
                .map(InterviewMapper::getInterviewDTO)
                .toList();
    }

    public Page<InterviewDTO> findPaging(int page, int size) {
        return interviewRepository.findAll(
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate")))
                .map(InterviewMapper::getInterviewDTO);
    }

    public Page<InterviewDTO> findPagingByUserIdRelated(int page, int size, int userId) {
        Page<Interview> interviews = wisherRepository.findInterviewByUserIdApproved(userId, Pageable.unpaged());
        List<Integer> interviewIds = interviews.stream().map(Interview::getId).toList();
        var status = StatusInterview.IS_NEW;
        return interviewRepository.findAllByUserIdRelated(userId, status, interviewIds,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate")))
                .map(InterviewMapper::getInterviewDTO);
    }

    public Optional<InterviewDTO> findById(Integer id) {
        var interview = interviewRepository.findById(id);
        if (interview.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(InterviewMapper.getInterviewDTO(interview.get()));
    }

    public List<InterviewDTO> findByMode(int mode) {
        return interviewRepository.findByMode(mode).stream()
                .peek(interview -> {
                    if (interview.getTopicId() == null) {
                        interview.setTopicId(1);
                    }
                }).map(InterviewMapper::getInterviewDTO)
                .toList();
    }

    public boolean update(InterviewDTO interviewDTO) {
        try {
            this.save(interviewDTO);
        } catch (Exception e) {
            log.error("Update interview error:{}", e);
            return false;
        }
        return true;
    }

    public void delete(int interviewId) {
        interviewRepository.deleteById(interviewId);
    }

    /**
     * Метод обновляет статус собеседования.
     *
     * @param interviewDTO InterviewDTO.
     * @return boolean true / false
     */
    public boolean updateStatus(InterviewDTO interviewDTO) {
        var newStatus = InterviewMapper.getStatusInterviewById(interviewDTO.getStatusId());
        try {
            interviewRepository.updateStatus(interviewDTO.getId(), newStatus);
            return true;
        } catch (Exception e) {
            log.error("Update status error {}", e.getMessage());
            return false;
        }
    }

    /**
     * Метод возвращает все Interview на которые пользователь должен оставить отзыв
     *
     * @param userId ID User
     * @return List<Interview>
     */
    public List<InterviewDTO> findAllIdByNoFeedback(int userId) {
        return interviewRepository.findAllByUserIdWisherIsApproveAndNoFeedback(userId)
                .stream()
                .map(InterviewMapper::getInterviewDTO)
                .toList();
    }

    /**
     * Метод возвращает все Interview со статусом новые
     *
     * @return List<Interview>
     */
    public List<InterviewDTO> findNewInterview() {
        var status = StatusInterview.IS_NEW;
        return interviewRepository.findAllByStatus(status)
                .stream()
                .map(InterviewMapper::getInterviewDTO)
                .toList();
    }

    public Page<InterviewDTO> getInterviewsWithFilters(
            int page, int size, FilterRequestParams filterRequestParams) {
        return interviewRepository.findAll(
                        interviewFilterSpecifications
                                .createSpecifications(filterRequestParams), PageRequest.of(page, size))
                .map(InterviewMapper::getInterviewDTO);
    }
}
