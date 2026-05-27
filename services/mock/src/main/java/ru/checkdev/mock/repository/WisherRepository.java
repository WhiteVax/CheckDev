package ru.checkdev.mock.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.checkdev.mock.domain.Interview;
import ru.checkdev.mock.domain.Wisher;
import ru.checkdev.mock.dto.UsersApprovedInterviewsDTO;
import ru.checkdev.mock.dto.WisherDto;

import java.util.List;
import java.util.Optional;

public interface WisherRepository extends CrudRepository<Wisher, Integer> {

    List<Wisher> findByInterview(Interview interview);

    List<Wisher> findAll();

    /**
     * Метод нативным запросом формирует список всех участников собеседований,
     * возвращая список DTO моделей WisherDTO
     *
     * @param interviewId int Interview ID
     * @return List<WisherDTO>
     */
    @Query("SELECT new ru.checkdev.mock.dto.WisherDto(w.id, w.interview.id, w.userId, w.contactBy, w.approve) FROM wisher w WHERE w.interview.id =:interviewId")
    List<WisherDto> findWisherDTOByInterviewId(@Param("interviewId") int interviewId);

    /**
     * Метод нативным запросом формирует список всех участников собеседований
     *
     * @return List<WisherDTO>
     */
    @Query("SELECT new ru.checkdev.mock.dto.WisherDto(w.id, w.interview.id, w.userId, w.contactBy, w.approve) FROM wisher w")
    List<WisherDto> findAllWiserDto();

    /**
     * Метод устанавливает одобренному участнику approved, признак одобрен или нет.
     *
     * @param interviewId ID interview
     * @param wisherId    ID wisher
     * @param approve     Boolean true/false
     */
    @Transactional
    @Modifying
    @Query(value = "UPDATE wisher w SET w.approve =:approve WHERE w.interview.id=:interviewId AND w.id=:wisherId ")
    void setWisherApprove(@Param("interviewId") int interviewId,
                          @Param("wisherId") int wisherId,
                          @Param("approve") boolean approve);

    /**
     * @param userId   int
     * @param pageable Pageable
     * @return интервью, в которых пользователь участвует и одобрен к участию
     */
    @Query("SELECT i FROM wisher w JOIN w.interview i WHERE w.userId = :userId AND w.approve IS TRUE")
    Page<Interview> findInterviewByUserIdApproved(@Param("userId") int userId, Pageable pageable);

    /**
     * Метод формирует список id пользователей с количеством их проведенных собеседований
     *
     * @return List<UsersApprovedInterviewsDTO>
     */
    @Query("""
            SELECT new ru.checkdev.mock.dto.UsersApprovedInterviewsDTO (w.userId, COUNT(w.approve))
            FROM wisher w
            WHERE w.approve = true
            GROUP BY w.userId""")
    List<UsersApprovedInterviewsDTO> getUsersIdWithCountedApprovedInterviews();

    /**
     * Метод формирует DTO с id пользователя и с количеством проведенных собеседований
     *
     * @param userId int
     * @return Optional<UsersApprovedInterviewsDTO>
     */
    @Query("""
            SELECT new ru.checkdev.mock.dto.UsersApprovedInterviewsDTO (w.userId, COUNT(w.approve))
            FROM wisher w
            WHERE w.approve = true AND w.userId = :userId
            GROUP BY w.userId""")
    Optional<UsersApprovedInterviewsDTO> getUserIdWithCountedApprovedInterviews(@Param("userId") int userId);
}