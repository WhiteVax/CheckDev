package ru.checkdev.desc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.checkdev.desc.domain.Category;
import ru.checkdev.desc.dto.CategoryDTO;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Integer> {

    /**
     * Метод возвращает все с категории в виде DTO.
     *
     * @return List<CategoryDTO>
     */
    @Query("""
            SELECT new ru.checkdev.desc.dto.CategoryDTO(c.id, c.name, c.total, COUNT(t.id), c.position)
            FROM cd_category c
            LEFT JOIN cd_topic t ON c.id = t.category.id
            GROUP BY c.id
            ORDER BY c.position
            """)
    List<CategoryDTO> getAllCategoryDTO();

    Iterable<Category> findAllByOrderByPositionAsc();

    @Query("""
            SELECT new ru.checkdev.desc.dto.CategoryDTO(c.id, c.name, c.total, COUNT(t.id), c.position)
            FROM cd_category c
            LEFT JOIN cd_topic t ON c.id = t.category.id
            GROUP BY c.id
            ORDER BY c.total DESC
            """)
    List<CategoryDTO> findAllByOrderTotalDescLimit(Pageable pageable);

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Modifying
    @Query("UPDATE cd_category c SET c.total = c.total + 1 WHERE c.id =:id")
    void updateStatistic(@Param("id") int id);
}
