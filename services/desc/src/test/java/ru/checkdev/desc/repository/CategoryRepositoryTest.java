package ru.checkdev.desc.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.checkdev.desc.domain.Category;
import ru.checkdev.desc.domain.Topic;
import ru.checkdev.desc.dto.CategoryDTO;

import javax.persistence.EntityManager;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@RunWith(SpringRunner.class)
class CategoryRepositoryTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void clearTable() {
        entityManager.createQuery("delete from cd_topic").executeUpdate();
        entityManager.createQuery("delete from cd_category").executeUpdate();
        entityManager.clear();
    }

    @Test
    void initRepositoryWhenNotNull() {
        assertThat(categoryRepository).isNotNull();
    }

    @Test
    void whenCreatedNewCategory() {
        var category = new Category();
        category.setName("Java SE");
        var saved = categoryRepository.save(category);
        assertThat(
                categoryRepository.findById(saved.getId())
        ).isEqualTo(Optional.of(saved));
    }

    @Test
    void whenFindAllByOrderByPositionAscThenReturnAscList() {
        var category1 = new Category();
        category1.setName("category1");
        category1.setPosition(44);
        var category2 = new Category();
        category2.setName("category2");
        category2.setPosition(4);
        entityManager.persist(category1);
        entityManager.persist(category2);
        entityManager.clear();
        var expectedList = List.of(category2, category1);
        var actualList = categoryRepository.findAllByOrderByPositionAsc();
        assertThat(actualList).usingRecursiveComparison().isEqualTo(expectedList);
    }

    @Test
    void whenUpdateStatistic2ThenReturnStatistic2() {
        int expectTotal = 2;
        var category1 = new Category();
        category1.setName("category1");
        category1.setPosition(44);
        entityManager.persist(category1);
        entityManager.clear();
        categoryRepository.updateStatistic(category1.getId());
        categoryRepository.updateStatistic(category1.getId());
        var categoryInDb = categoryRepository.findById(category1.getId());
        assertThat(categoryInDb).isNotEmpty();
        assertThat(categoryInDb.get().getTotal()).isEqualTo(expectTotal);
    }

    @Test
    void whenUpdateStatistic2ThenReturnStatistic5() {
        int expectTotal = 5;
        var category1 = new Category();
        category1.setName("category1");
        category1.setPosition(44);
        entityManager.persist(category1);
        entityManager.clear();
        categoryRepository.updateStatistic(category1.getId());
        categoryRepository.updateStatistic(category1.getId());
        categoryRepository.updateStatistic(category1.getId());
        categoryRepository.updateStatistic(category1.getId());
        categoryRepository.updateStatistic(category1.getId());
        var categoryInDb = categoryRepository.findById(category1.getId());
        assertThat(categoryInDb).isNotEmpty();
        assertThat(categoryInDb.get().getTotal()).isEqualTo(expectTotal);
    }

    @Test
    void whenAdd2CategoryFindAllByOrderTotalDescLimit1ThenReturnSize1() {
        var limit = 1;
        var category1 = new Category();
        category1.setName("category1");
        category1.setTotal(20);
        var category2 = new Category();
        category2.setName("category2");
        category2.setTotal(44);
        entityManager.persist(category1);
        entityManager.persist(category2);
        entityManager.clear();
        var categoryDto1 = new CategoryDTO();
        categoryDto1.setId(category1.getId());
        categoryDto1.setName(category1.getName());
        categoryDto1.setTotal(category1.getTotal());
        var categoryDto2 = new CategoryDTO();
        categoryDto2.setId(category2.getId());
        categoryDto2.setName(category2.getName());
        categoryDto2.setTotal(category2.getTotal());
        var actual = categoryRepository.findAllByOrderTotalDescLimit(PageRequest.of(0, limit));
        var expect = List.of(categoryDto2);
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void whenAdd2CategoryFindAllByOrderTotalDescLimit2ThenReturnSize2() {
        var limit = 2;
        var category1 = new Category();
        category1.setName("category1");
        category1.setTotal(20);
        var category2 = new Category();
        category2.setName("category2");
        category2.setTotal(44);
        entityManager.persist(category1);
        entityManager.persist(category2);
        entityManager.clear();
        var categoryDto1 = new CategoryDTO();
        categoryDto1.setId(category1.getId());
        categoryDto1.setName(category1.getName());
        categoryDto1.setTotal(category1.getTotal());
        var categoryDto2 = new CategoryDTO();
        categoryDto2.setId(category2.getId());
        categoryDto2.setName(category2.getName());
        categoryDto2.setTotal(category2.getTotal());
        var actual = categoryRepository.findAllByOrderTotalDescLimit(PageRequest.of(0, limit));
        var expect = List.of(categoryDto2, categoryDto1);
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void whenAdd5CategoryFindAllByOrderTotalDescLimit4ThenReturnSize42() {
        var limit = 4;
        var category1 = new Category();
        category1.setName("category1");
        category1.setTotal(20);
        var category2 = new Category();
        category2.setName("category2");
        category2.setTotal(44);
        var category3 = new Category();
        category3.setName("category3");
        category3.setTotal(15);
        var category4 = new Category();
        category4.setName("category4");
        category4.setTotal(60);
        var category5 = new Category();
        category5.setName("category5");
        category5.setTotal(3);
        entityManager.persist(category1);
        entityManager.persist(category2);
        entityManager.persist(category3);
        entityManager.persist(category4);
        entityManager.persist(category5);
        entityManager.clear();
        var categoryDto1 = new CategoryDTO();
        categoryDto1.setId(category1.getId());
        categoryDto1.setName(category1.getName());
        categoryDto1.setTotal(category1.getTotal());
        var categoryDto2 = new CategoryDTO();
        categoryDto2.setId(category2.getId());
        categoryDto2.setName(category2.getName());
        categoryDto2.setTotal(category2.getTotal());
        var categoryDto3 = new CategoryDTO();
        categoryDto3.setId(category3.getId());
        categoryDto3.setName(category3.getName());
        categoryDto3.setTotal(category3.getTotal());
        var categoryDto4 = new CategoryDTO();
        categoryDto4.setId(category4.getId());
        categoryDto4.setName(category4.getName());
        categoryDto4.setTotal(category4.getTotal());
        var actual = categoryRepository.findAllByOrderTotalDescLimit(PageRequest.of(0, limit));
        var expect = List.of(categoryDto4, categoryDto2, categoryDto1, categoryDto3);
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void whenGetAllCategoryDTOThenEmpty() {
        var actual = categoryRepository.getAllCategoryDTO();
        assertThat(actual).isEmpty();
    }

    @Test
    void whenGetAllCategoryDTOThenReturnListSizeOneTopicSizeTwo() {
        var created = Calendar.getInstance();
        created.set(2_023, 11, 24, 22, 05, 00);
        var category = new Category(0, "name1", 1, 1);
        entityManager.persist(category);
        var topic1 = Topic.of()
                .name("topic1")
                .text("text1")
                .created(created)
                .total(1)
                .position(2)
                .category(category)
                .build();
        var topic2 = Topic.of()
                .name("topic2")
                .text("text2")
                .created(created)
                .total(2)
                .position(3)
                .category(category)
                .build();
        entityManager.persist(topic1);
        entityManager.persist(topic2);
        entityManager.clear();
        var categoryDTO = CategoryDTO.of()
                .id(category.getId())
                .name(category.getName())
                .total(category.getTotal())
                .topicsSize(2L)
                .position(category.getPosition())
                .build();
        var expected = List.of(categoryDTO);
        var actual = categoryRepository.getAllCategoryDTO();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void whenGetAllCategoryDTOThenReturnListSizeOneTopicSizeOne() {
        var created = Calendar.getInstance();
        created.set(2023, 11, 24, 22, 05, 00);
        var category = new Category(0, "name1", 1, 1);
        entityManager.persist(category);
        var topic1 = Topic.of()
                .name("topic1")
                .text("text1")
                .created(created)
                .total(1)
                .position(2)
                .category(category)
                .build();
        entityManager.persist(topic1);
        entityManager.clear();
        var categoryDTO = CategoryDTO.of()
                .id(category.getId())
                .name(category.getName())
                .total(category.getTotal())
                .topicsSize(1L)
                .position(category.getPosition())
                .build();
        var expected = List.of(categoryDTO);
        var actual = categoryRepository.getAllCategoryDTO();
        assertThat(actual).isEqualTo(expected);
    }
}