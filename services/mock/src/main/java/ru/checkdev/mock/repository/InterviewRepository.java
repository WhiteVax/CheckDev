package ru.checkdev.mock.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.checkdev.mock.domain.Interview;
import ru.checkdev.mock.enums.StatusInterview;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Integer> {

    @Query("SELECT i FROM interview i"
            + " WHERE i.submitterId=:userId"
            + " OR i.status=:status"
            + " OR i.id IN (:interviewIds)"
    )
    Page<Interview> findAllByUserIdRelated(@Param("userId") int userId,
                                           @Param("status") StatusInterview status,
                                           @Param("interviewIds") List<Integer> interviewIds,
                                           Pageable pageable);

    List<Interview> findByMode(int mode);

    /**
     * Метод обновляет статус собеседования.
     *
     * @param id     ID Interview
     * @param status Status
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE interview i SET i.status=:status WHERE i.id=:id")
    void updateStatus(@Param("id") int id, @Param("status") StatusInterview status);

    /**
     * Возвращает все собеседования на который пользователь должен оставить отзыв.
     * nativeQuery = true;
     * Описание построения запроса:
     * Через внутренние объединения получаем список всех собеседований,
     * которые присутствуют в таблице wisher с признаком approve=true,
     * а также которые принадлежат указанному пользователю.
     * Конечная выборка получает все ID собеседований которых нет в таблице cd_feedback.
     * Ожидаемое поведение: пользователь не владелец собеседования,
     * но он одобренный участник и не оставил отзыв, метод вернет список с ID этого собеседования.
     * Пользователь является автором собеседования и он одобрил участника и не оставил отзыв,
     * метод вернет список с ID этого собеседования.
     * Так же если пользователь уже оставил отзыв на собеседование с ID то это собеседование не попадает в выборку.
     *
     * @param userId ID User
     * @return List<Interview>
     */
    @Query(value = """
            SELECT DISTINCT i.*
            FROM interview i
                     JOIN wisher w ON i.id = w.interview_id AND w.approve AND (i.submitter_id = :userId OR w.user_id = :userId)
            WHERE NOT EXISTS(SELECT 1
                             FROM cd_feedback cf
                             WHERE cf.interview_id = i.id
                               AND cf.user_id = :userId)
            """, nativeQuery = true)
    List<Interview> findAllByUserIdWisherIsApproveAndNoFeedback(@Param("userId") int userId);

    /**
     * Получаем из базы ТРИ последние интервью отсортированные по убыванию по дате их создания.
     *
     * @return LIST из ТРЕХ последних интервью
     */
    List<Interview> findAllByStatusOrderByCreateDateDesc(StatusInterview status, Pageable pageable);

    /**
     * Получаем из базы новые интервью по статусу.
     *
     * @return LIST из новых интервью
     */
    List<Interview> findAllByStatus(StatusInterview status);

    Page<Interview> findAll(Specification<Interview> specification, Pageable pageable);
}
