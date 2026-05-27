package ru.checkdev.mock.repository;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.checkdev.mock.domain.Feedback;
import ru.checkdev.mock.domain.Interview;
import ru.checkdev.mock.domain.Wisher;
import ru.checkdev.mock.enums.StatusInterview;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest()
@RunWith(SpringRunner.class)
class InterviewRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private InterviewRepository interviewRepository;

    @BeforeEach
    void clearTables() {
        entityManager.createQuery("delete from cd_feedback").executeUpdate();
        entityManager.createQuery("delete from wisher").executeUpdate();
        entityManager.createQuery("delete from interview").executeUpdate();
        entityManager.clear();
    }

    @Test
    public void injectedComponentAreNotNull() {
        Assertions.assertNotNull(entityManager);
        Assertions.assertNotNull(interviewRepository);
    }

    @Test
    public void whenInterviewFindByType() {
        var interview = new Interview();
        interview.setMode(1);
        interview.setSubmitterId(1);
        interview.setTitle("title");
        interview.setAdditional("additional");
        interview.setContactBy("contact");
        interview.setApproximateDate("30.02.2070");
        interview.setCreateDate(new Timestamp(System.currentTimeMillis()));
        interview.setTopicId(1);
        interview.setAuthor("author");
        entityManager.persist(interview);
        var interviews = interviewRepository.findByMode(1);
        assertTrue(interviews.size() > 0);
        Assertions.assertEquals(interviews.get(0), interview);
    }

    @Test
    public void whenInterviewNotFoundByType() {
        var interviews = interviewRepository.findByMode(1);
        Assertions.assertEquals(0, interviews.size());
    }

    @Test
    public void whenFindAllInterview() {
        var listInterview = interviewRepository.findAll();
        MatcherAssert.assertThat(listInterview, is(Collections.emptyList()));
    }

    @Test
    public void whenFindAllByUserIdRelatedAndNothingFound() {
        var interview = new Interview();
        interview.setMode(1);
        interview.setSubmitterId(1);
        interview.setTitle("title");
        interview.setAdditional("additional");
        interview.setContactBy("contact");
        interview.setApproximateDate("30.02.2070");
        interview.setCreateDate(new Timestamp(System.currentTimeMillis()));
        interview.setTopicId(1);
        interview.setAuthor("author");
        entityManager.persist(interview);
        int userId = 2;
        var status = StatusInterview.IS_NEW;
        var page = interviewRepository.findAllByUserIdRelated(userId, status, List.of(1), PageRequest.of(0, 10));
        MatcherAssert.assertThat(page.getTotalElements(), is(0L));
    }

    @Test
    public void whenFindAllByUserIdRelatedAndFound() {
        var interview = new Interview();
        interview.setMode(1);
        interview.setSubmitterId(1);
        interview.setTitle("title");
        interview.setAdditional("additional");
        interview.setContactBy("contact");
        interview.setApproximateDate("30.02.2070");
        interview.setCreateDate(new Timestamp(System.currentTimeMillis()));
        interview.setTopicId(1);
        interview.setAuthor("author");
        entityManager.persist(interview);
        int userId = 1;
        var status = StatusInterview.IS_NEW;
        var page = interviewRepository.findAllByUserIdRelated(userId, status, List.of(1), PageRequest.of(0, 10));
        MatcherAssert.assertThat(page.getTotalElements(), is(1L));
    }

    @Test
    public void whenUpdateStatusInterviewThenUpdateStatus() {
        var newStatus = StatusInterview.IS_CANCELED;
        var interview = new Interview();
        interview.setMode(1);
        interview.setStatus(StatusInterview.IS_NEW);
        interview.setSubmitterId(1);
        interview.setTitle("title");
        interview.setAdditional("additional");
        interview.setContactBy("contact");
        interview.setApproximateDate("30.02.2070");
        interview.setCreateDate(new Timestamp(System.currentTimeMillis()));
        interview.setTopicId(1);
        interview.setAuthor("author");
        entityManager.persist(interview);
        entityManager.clear();
        interviewRepository.updateStatus(interview.getId(), newStatus);
        var interviewInDb = interviewRepository.findById(interview.getId());
        assertThat(interviewInDb.isPresent()).isTrue();
        assertThat(interviewInDb.get().getStatus()).isEqualTo(newStatus);
    }

    @Test
    public void whenUpdateStatusInterviewThenNotUpdateStatus() {
        var newStatus = StatusInterview.IS_CANCELED;
        var interview = new Interview();
        interview.setMode(1);
        interview.setStatus(StatusInterview.IS_NEW);
        interview.setSubmitterId(1);
        interview.setTitle("title");
        interview.setAdditional("additional");
        interview.setContactBy("contact");
        interview.setApproximateDate("30.02.2070");
        interview.setCreateDate(new Timestamp(System.currentTimeMillis()));
        interview.setTopicId(1);
        interview.setAuthor("author");
        entityManager.persist(interview);
        entityManager.clear();
        interviewRepository.updateStatus(interview.getId() + 99, newStatus);
        var interviewInDb = interviewRepository.findById(interview.getId());
        assertThat(interviewInDb.isPresent()).isTrue();
        assertThat(interviewInDb.get().getStatus()).isEqualTo(interview.getStatus());
    }

    @Test
    void whenFindAllNoFeedbackWhenReturnEmptyList() {
        List<Interview> actual = interviewRepository.findAllByUserIdWisherIsApproveAndNoFeedback(-1);
        assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    void whenFindAllByUserIdSubmitterAndUserWisherNoFeedbackWhenReturnListInterview() {
        int submitterId = 1;
        int userWisherId = 2;
        Interview interview = Interview.of()
                .mode(1)
                .submitterId(submitterId)
                .title("title")
                .contactBy("contact")
                .approximateDate("now")
                .createDate(Timestamp.valueOf("2023-11-06 00:00:00"))
                .topicId(1)
                .author("author")
                .build();
        entityManager.persist(interview);
        Wisher wisher = Wisher.of()
                .userId(userWisherId)
                .interview(interview)
                .approve(true)
                .build();
        entityManager.persist(wisher);
        entityManager.clear();
        List<Interview> expected = List.of(interview);
        List<Interview> actualBySubmitter =
                interviewRepository.findAllByUserIdWisherIsApproveAndNoFeedback(interview.getSubmitterId());
        List<Interview> actualByWisherUser =
                interviewRepository.findAllByUserIdWisherIsApproveAndNoFeedback(wisher.getUserId());
        assertThat(actualBySubmitter).isEqualTo(expected);
        assertThat(actualByWisherUser).isEqualTo(expected);
    }

    @Test
    void whenFindAllByUserIdSubmitterAndUserWisherNoFeedbackWhenReturnListInterviewSubmitter() {
        int submitterId = 1;
        int userWisherId = 2;
        Interview interview = Interview.of()
                .mode(1)
                .submitterId(submitterId)
                .title("title")
                .contactBy("contact")
                .approximateDate("now")
                .createDate(Timestamp.valueOf("2023-11-06 00:00:00"))
                .topicId(1)
                .author("author")
                .build();
        entityManager.persist(interview);
        Wisher wisher = Wisher.of()
                .userId(userWisherId)
                .interview(interview)
                .approve(true)
                .build();
        entityManager.persist(wisher);
        Feedback feedback = Feedback.of()
                .interview(interview)
                .userId(wisher.getUserId())
                .textFeedback("text")
                .build();
        entityManager.persist(feedback);
        entityManager.clear();
        List<Interview> expected = List.of(interview);
        List<Interview> actualBySubmitter =
                interviewRepository.findAllByUserIdWisherIsApproveAndNoFeedback(interview.getSubmitterId());
        List<Interview> actualByWisherUser =
                interviewRepository.findAllByUserIdWisherIsApproveAndNoFeedback(wisher.getUserId());
        assertThat(actualBySubmitter).isEqualTo(expected);
        assertThat(actualByWisherUser.isEmpty()).isTrue();
    }

    @Test
    void whenFindAllByUserIdSubmitterAndUserWisherNoFeedbackWhenReturnListInterviewWisherUser() {
        int submitterId = 1;
        int userWisherId = 2;
        Interview interview = Interview.of()
                .mode(1)
                .submitterId(submitterId)
                .title("title")
                .contactBy("contact")
                .approximateDate("now")
                .createDate(Timestamp.valueOf("2023-11-06 00:00:00"))
                .topicId(1)
                .author("author")
                .build();
        entityManager.persist(interview);
        Wisher wisher = Wisher.of()
                .userId(userWisherId)
                .interview(interview)
                .approve(true)
                .build();
        entityManager.persist(wisher);
        Feedback feedback = Feedback.of()
                .interview(interview)
                .userId(interview.getSubmitterId())
                .textFeedback("text")
                .build();
        entityManager.persist(feedback);
        entityManager.clear();
        List<Interview> expected = List.of(interview);
        List<Interview> actualBySubmitter =
                interviewRepository.findAllByUserIdWisherIsApproveAndNoFeedback(interview.getSubmitterId());
        List<Interview> actualByWisherUser =
                interviewRepository.findAllByUserIdWisherIsApproveAndNoFeedback(wisher.getUserId());
        assertThat(actualBySubmitter.isEmpty()).isTrue();
        assertThat(actualByWisherUser).isEqualTo(expected);
    }

    @Test
    void whenFindAllByUserIdSubmitterAndUserWisherApproveFalseNoFeedbackWhenListEmpty() {
        int submitterId = 1;
        int userWisherId = 2;
        Interview interview = Interview.of()
                .mode(1)
                .submitterId(submitterId)
                .title("title")
                .contactBy("contact")
                .approximateDate("now")
                .createDate(Timestamp.valueOf("2023-11-06 00:00:00"))
                .topicId(1)
                .author("author")
                .build();
        entityManager.persist(interview);
        Wisher wisher = Wisher.of()
                .userId(userWisherId)
                .interview(interview)
                .approve(false)
                .build();
        entityManager.persist(wisher);
        entityManager.clear();
        List<Interview> actualBySubmitter =
                interviewRepository.findAllByUserIdWisherIsApproveAndNoFeedback(interview.getSubmitterId());
        List<Interview> actualByWisherUser =
                interviewRepository.findAllByUserIdWisherIsApproveAndNoFeedback(wisher.getUserId());
        assertThat(actualBySubmitter.isEmpty()).isTrue();
        assertThat(actualByWisherUser.isEmpty()).isTrue();
    }

    @Test
    void whenFindAllNotFeedbackIsSubmitterFeedbackAndWisherFeedbackThenReturnEmptyList() {
        int submitterId = 1;
        int userWisherId = 2;
        Interview interview = Interview.of()
                .mode(1)
                .submitterId(submitterId)
                .title("title")
                .contactBy("contact")
                .approximateDate("now")
                .createDate(Timestamp.valueOf("2023-11-06 00:00:00"))
                .topicId(1)
                .author("author")
                .build();
        entityManager.persist(interview);
        Wisher wisher = Wisher.of()
                .userId(userWisherId)
                .interview(interview)
                .approve(true)
                .build();
        entityManager.persist(wisher);
        Feedback feedbackSubmitter = Feedback.of()
                .interview(interview)
                .userId(interview.getSubmitterId())
                .textFeedback("textSubmitter")
                .build();
        entityManager.persist(feedbackSubmitter);
        Feedback feedbackWisher = Feedback.of()
                .interview(interview)
                .userId(wisher.getUserId())
                .textFeedback("textUser")
                .build();
        entityManager.persist(feedbackWisher);
        entityManager.clear();
        List<Interview> actualBySubmitter =
                interviewRepository.findAllByUserIdWisherIsApproveAndNoFeedback(interview.getSubmitterId());
        List<Interview> actualByWisherUser =
                interviewRepository.findAllByUserIdWisherIsApproveAndNoFeedback(wisher.getUserId());
        assertThat(actualBySubmitter.isEmpty()).isTrue();
        assertThat(actualByWisherUser.isEmpty()).isTrue();
    }

}