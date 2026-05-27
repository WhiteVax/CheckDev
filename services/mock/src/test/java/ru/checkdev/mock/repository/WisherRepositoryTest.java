package ru.checkdev.mock.repository;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.checkdev.mock.domain.Interview;
import ru.checkdev.mock.domain.Wisher;
import ru.checkdev.mock.dto.UsersApprovedInterviewsDTO;
import ru.checkdev.mock.dto.WisherDto;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@DataJpaTest()
@RunWith(SpringRunner.class)
class WisherRepositoryTest {
    private Interview interview1;
    private Interview interview2;
    private Interview interview3;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private WisherRepository wisherRepository;

    @BeforeEach
    public void clearTable() {
        entityManager.createQuery("delete from wisher").executeUpdate();
        entityManager.createQuery("delete from interview").executeUpdate();
        interview1 = Interview.of().mode(1).submitterId(1).title("title").additional("additional")
                .contactBy("mail@mail").approximateDate("approximate")
                .createDate(new Timestamp(System.currentTimeMillis()))
                .topicId(1).author("author")
                .build();
        interview2 = Interview.of().mode(1).submitterId(1).title("title").additional("additional")
                .contactBy("mail@mail").approximateDate("approximate")
                .createDate(new Timestamp(System.currentTimeMillis()))
                .topicId(2).author("author")
                .build();
        interview3 = Interview.of().mode(1).submitterId(1).title("title").additional("additional")
                .contactBy("mail@mail").approximateDate("approximate")
                .createDate(new Timestamp(System.currentTimeMillis()))
                .topicId(2).author("author")
                .build();
        entityManager.persist(interview1);
        entityManager.persist(interview2);
        entityManager.persist(interview3);
        entityManager.flush();
    }

    @Test
    public void injectedComponentAreNotNull() {
        assertNotNull(entityManager);
        assertNotNull(wisherRepository);
    }

    @Test
    public void whenFindInterviewByIdThenReturnEmpty() {
        Optional<Wisher> wisher = wisherRepository.findById(-1);
        assertThat(wisher, is(Optional.empty()));
    }

    @Test
    public void whenFindAllInterview() {
        var listWisher = wisherRepository.findAll();
        assertThat(listWisher, is(Collections.emptyList()));
    }

    @Test
    public void whenFindWisherByInterviewIdThenReturnListWisherDto() {
        var userId = 1;
        var wisher = new Wisher(0, interview1, userId, "user_Mail", false);
        entityManager.persist(wisher);
        entityManager.flush();
        var expect = List.of(
                new WisherDto(
                        wisher.getId(), wisher.getInterview().getId(),
                        wisher.getUserId(), wisher.getContactBy(), wisher.isApprove()));
        var actual = wisherRepository.findWisherDTOByInterviewId(interview1.getId());
        assertThat(actual, is(expect));
    }

    @Test
    public void whenFindWisherByInterviewIdThenReturnEmptyList() {
        var userId = 1;
        var wisher = new Wisher(0, interview1, userId, "user_Mail", false);
        entityManager.persist(wisher);
        entityManager.flush();
        var actual = wisherRepository.findWisherDTOByInterviewId(-1);
        assertThat(actual, is(Collections.emptyList()));
    }

    @Test
    public void whenFindAllWisherDtoThenReturnListWisherDto() {
        var wisher1 = new Wisher(0, interview1, 1, "user_Mail1", false);
        var wisher2 = new Wisher(0, interview1, 2, "user_Mail2", false);
        entityManager.persist(wisher1);
        entityManager.persist(wisher2);
        entityManager.flush();
        var expect = List.of(
                new WisherDto(
                        wisher1.getId(), wisher1.getInterview().getId(),
                        wisher1.getUserId(), wisher1.getContactBy(), wisher1.isApprove()),
                new WisherDto(
                        wisher2.getId(), wisher2.getInterview().getId(),
                        wisher2.getUserId(), wisher2.getContactBy(),
                        wisher2.isApprove())
        );
        var actual = wisherRepository.findAllWiserDto();
        assertThat(actual, is(expect));
    }

    @Test
    public void whenFindAllWisherDtoThenReturnEmptyList() {
        var actual = wisherRepository.findAllWiserDto();
        assertThat(actual, is(Collections.emptyList()));
    }

    @Test
    void whenSetWisherApproveFalseThenReturnWisherNewApprove() {
        var userId1 = 1;
        var userId2 = 2;
        var wisher1 = new Wisher(0, interview1, userId1, "user_Mail1", false);
        var wisher2 = new Wisher(0, interview1, userId2, "user_Mail2", false);
        entityManager.persist(wisher1);
        entityManager.persist(wisher2);
        entityManager.clear();
        wisherRepository.setWisherApprove(interview1.getId(), wisher1.getId(), true);
        var wisherInDB = wisherRepository.findById(wisher1.getId());
        assertTrue(wisherInDB.isPresent());
        assertTrue(wisherInDB.get().isApprove());
    }

    @Test
    void whenSetWisherApproveThenReturnOldApprove() {
        var userId = 1;
        var wisherId = -1;
        var wisher = new Wisher(0, interview1, userId, "user_Mail", false);
        entityManager.persist(wisher);
        entityManager.clear();
        wisherRepository.setWisherApprove(interview1.getId(), wisherId, true);
        var wisherInDb = wisherRepository.findById(wisher.getId());
        assertTrue(wisherInDb.isPresent());
        assertFalse(wisherInDb.get().isApprove());
    }

    @Test
    void whenFindInterviewsFromWisherByUserIdApproved() {
        entityManager.persist(
                new Wisher(0, interview1, 1, "user_Mail1", true));
        entityManager.persist(
                new Wisher(0, interview2, 1, "user_Mail1", true));
        entityManager.persist(
                new Wisher(0, interview3, 1, "user_Mail1", false));
        entityManager.persist(
                new Wisher(0, interview3, 2, "user_Mail2", true));
        entityManager.clear();

        var page1 =
                wisherRepository.findInterviewByUserIdApproved(1, PageRequest.of(0, 10));
        var page2 =
                wisherRepository.findInterviewByUserIdApproved(2, PageRequest.of(0, 10));
        var page3 =
                wisherRepository.findInterviewByUserIdApproved(1349, PageRequest.of(0, 10));

        MatcherAssert.assertThat(page1.toList().size(), is(2));
        MatcherAssert.assertThat(page1.toList().get(0), is(interview1));
        MatcherAssert.assertThat(page2.toList().size(), is(1));
        MatcherAssert.assertThat(page3.toList().size(), is(0));
    }

    @Test
    void whenFindInterviewsFromWisherByUserIdApprovedAndNothingFound() {
        entityManager.persist(
                new Wisher(0, interview1, 1, "user_Mail1", false));
        entityManager.persist(
                new Wisher(0, interview2, 1, "user_Mail1", false));
        entityManager.persist(
                new Wisher(0, interview3, 1, "user_Mail1", false));
        entityManager.persist(
                new Wisher(0, interview3, 2, "user_Mail2", false));
        entityManager.clear();

        var page1 =
                wisherRepository.findInterviewByUserIdApproved(1, PageRequest.of(0, 10));
        var page2 =
                wisherRepository.findInterviewByUserIdApproved(2, PageRequest.of(0, 10));
        var page3 =
                wisherRepository.findInterviewByUserIdApproved(1349, PageRequest.of(0, 10));

        MatcherAssert.assertThat(page1.toList().size(), is(0));
        MatcherAssert.assertThat(page2.toList().size(), is(0));
        MatcherAssert.assertThat(page3.toList().size(), is(0));
    }

    @Test
    void whenGetUsersIdWithCountedApprovedInterviewsAndDatabaseEmptyThenGetEmptyList() {
        var actual = wisherRepository.getUsersIdWithCountedApprovedInterviews();
        assertThat(actual, is(Collections.emptyList()));
    }

    @Test
    void whenGetUsersIdWithCountedApprovedInterviewsThenGetListOfDtoOnlyWithApprovedInterviews() {
        entityManager.persist(
                new Wisher(0, interview1, 1, "user_Mail1", false));
        entityManager.persist(
                new Wisher(0, interview2, 1, "user_Mail1", true));
        entityManager.persist(
                new Wisher(0, interview3, 1, "user_Mail1", true));
        entityManager.persist(
                new Wisher(0, interview3, 2, "user_Mail2", true));
        entityManager.clear();

        var expected = List.of(
                new UsersApprovedInterviewsDTO(1, 2),
                new UsersApprovedInterviewsDTO(2, 1)
        );
        var actual = wisherRepository.getUsersIdWithCountedApprovedInterviews();

        assertThat(actual, is(expected));
    }


    @Test
    void whenGetUserIdWithCountedApprovedInterviewsAndDatabaseEmptyThenGetOptionalEmpty() {
        var actual = wisherRepository.getUserIdWithCountedApprovedInterviews(1);
        assertTrue(actual.isEmpty());
    }

    @Test
    void whenGetUserIdWithCountedApprovedInterviewsAndInterviewNotApprovedThenGetOptionalEmpty() {
        entityManager.persist(
                new Wisher(0, interview1, 1, "user_Mail1", false));
        entityManager.clear();
        var actual = wisherRepository.getUserIdWithCountedApprovedInterviews(1);
        assertTrue(actual.isEmpty());
    }

    @Test
    void whenGetUserIdWithCountedApprovedInterviewsThenGetOptionalOfOfDtoOnlyWithApprovedInterviews() {
        entityManager.persist(
                new Wisher(0, interview1, 1, "user_Mail1", false));
        entityManager.persist(
                new Wisher(0, interview2, 1, "user_Mail1", true));
        entityManager.clear();

        var expected = Optional.of(new UsersApprovedInterviewsDTO(1, 1));
        var actual = wisherRepository.getUserIdWithCountedApprovedInterviews(1);

        assertThat(actual, is(expected));
    }

}