package ru.checkdev.desc.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.checkdev.desc.domain.Topic;
import ru.checkdev.desc.dto.CategoryIdNameDTO;
import ru.checkdev.desc.dto.TopicDTO;
import ru.checkdev.desc.dto.TopicLiteDTO;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends CrudRepository<Topic, Integer> {
    List<Topic> findAllByOrderByPositionAsc();

    List<Topic> findByCategoryIdOrderByPositionAsc(Integer categoryId);

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Modifying
    @Query("update cd_topic t set t.total = t.total + 1 where t.id=:id")
    void incrementTotal(@Param("id") int id);

    @Query("SELECT t.name FROM cd_topic t WHERE t.id = :id")
    Optional<String> getNameById(@Param("id") int id);

    @Query("""
            SELECT new ru.checkdev.desc.dto.TopicDTO(t.id, t.name)
            FROM cd_topic t WHERE t.category.id = :categoryId
            """)
    List<TopicDTO> findIdAndNameByCategoryId(@Param("categoryId") Integer categoryId);

    @Query("""
            SELECT new ru.checkdev.desc.dto.CategoryIdNameDTO(c.id, c.name)
            FROM cd_category c
            JOIN cd_topic t ON c.id = t.category.id AND t.id = :id 
            """)
    Optional<CategoryIdNameDTO> findCategoryIdAndNameById(@Param("id") Integer id);

    /**
     * Метод собирает список всех topic в модель TopicLiteDTO
     *
     * @return List<TopicLiteDTO>
     */
    @Query("""
            SELECT 
            new ru.checkdev.desc.dto.TopicLiteDTO(ct.id, ct.name, ct.text, cc.id, cc.name, ct.position) 
            FROM cd_category cc 
            JOIN cd_topic ct ON cc.id = ct.category.id
            """)
    List<TopicLiteDTO> getAllTopicLiteDTO();

    /**
     * Метод возвращает Optional TopicLiteDTO по Topic ID
     *
     * @param tId ID Topic
     * @return Optional<TopicLiteDTO>
     */
    @Query("""
            SELECT 
            new ru.checkdev.desc.dto.TopicLiteDTO(ct.id, ct.name, ct.text, cc.id, cc.name, ct.position) 
            FROM cd_category cc 
            JOIN cd_topic ct ON cc.id = ct.category.id 
            WHERE ct.id =:tId
            """)
    Optional<TopicLiteDTO> getTopicLiteDTOById(@Param("tId") int tId);
}
