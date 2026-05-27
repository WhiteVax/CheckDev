package ru.checkdev.desc.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.checkdev.desc.domain.Category;
import ru.checkdev.desc.domain.Topic;
import ru.checkdev.desc.dto.TopicLiteDTO;

import javax.persistence.EntityManager;
import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TopicRepository TEST
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 16.10.2023
 */
@DataJpaTest
@RunWith(SpringRunner.class)
class TopicRepositoryTest {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private TopicRepository topicRepository;

    private Category category1 = new Category();
    private Category category2 = new Category();

    @BeforeEach
    void clearTable() {
        entityManager.createQuery("delete from cd_topic").executeUpdate();
        entityManager.createQuery("delete from cd_category").executeUpdate();
        entityManager.clear();
    }

    @Test
    void injectedNotNul() {
        assertThat(entityManager).isNotNull();
        assertThat(topicRepository).isNotNull();
    }

    @BeforeEach
    void addCategoryInDataBaseFromTest() {
        category1.setName("category1");
        category1.setTotal(1);
        category1.setPosition(1);
        category2.setName("category2");
        category2.setTotal(2);
        category2.setPosition(2);
        entityManager.persist(category1);
        entityManager.persist(category2);
        entityManager.clear();
    }


    @Test
    void initRepositoryWhenNotNull() {
        assertThat(topicRepository).isNotNull();
    }

    @Test
    void whenSaveCategoryThenFindItsName() {
        var topic = new Topic();
        topic.setCategory(category1);
        topic.setText("text");
        topic.setPosition(1);
        topic.setName("Stream API");
        entityManager.persist(topic);
        var saved = topicRepository.save(topic);
        var name = topicRepository.getNameById(saved.getId());
        Assertions.assertTrue(name.isPresent());
        assertThat("Stream API").isEqualTo(name.get());
    }

    @Test
    void whenFindAllByOrderByPositionAscThenReturnListTopic() {
        var topic1 = new Topic();
        topic1.setName("topic1");
        topic1.setCategory(category1);
        topic1.setPosition(55);
        var topic2 = new Topic();
        topic2.setName("topic2");
        topic2.setCategory(category1);
        topic2.setPosition(10);
        entityManager.persist(topic1);
        entityManager.persist(topic2);
        entityManager.clear();
        var expectedList = List.of(topic2, topic1);
        var actualList = topicRepository.findAllByOrderByPositionAsc();
        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    void whenFindByCategory2IdOrderByPositionAsc() {
        var topic1 = new Topic();
        topic1.setName("topic1");
        topic1.setCategory(category1);
        topic1.setPosition(55);
        var topic2 = new Topic();
        topic2.setName("topic2");
        topic2.setCategory(category1);
        topic2.setPosition(10);
        var topic3 = new Topic();
        topic3.setName("topic1");
        topic3.setCategory(category2);
        topic3.setPosition(15);
        var topic4 = new Topic();
        topic4.setName("topic2");
        topic4.setCategory(category2);
        topic4.setPosition(5);
        entityManager.persist(topic1);
        entityManager.persist(topic2);
        entityManager.persist(topic3);
        entityManager.persist(topic4);
        entityManager.clear();
        var expectedList = List.of(topic4, topic3);
        var actualList = topicRepository.findByCategoryIdOrderByPositionAsc(category2.getId());
        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    void whenIncrementTotalTwoThenReturnTotal2() {
        int expectTotalSize2 = 2;
        var topic1 = new Topic();
        topic1.setName("topic1");
        topic1.setCategory(category1);
        topic1.setPosition(55);
        entityManager.persist(topic1);
        entityManager.clear();
        topicRepository.incrementTotal(topic1.getId());
        topicRepository.incrementTotal(topic1.getId());
        entityManager.clear();
        var topicInDb = topicRepository.findById(topic1.getId());
        assertThat(topicInDb).isNotEmpty();
        assertThat(topicInDb.get().getTotal()).isEqualTo(expectTotalSize2);
    }

    @Test
    void whenIncrementTotalTwoThenReturnTotal5() {
        int expectTotalSize5 = 5;
        var topic1 = new Topic();
        topic1.setName("topic1");
        topic1.setCategory(category1);
        topic1.setPosition(55);
        entityManager.persist(topic1);
        entityManager.clear();
        topicRepository.incrementTotal(topic1.getId());
        topicRepository.incrementTotal(topic1.getId());
        topicRepository.incrementTotal(topic1.getId());
        topicRepository.incrementTotal(topic1.getId());
        topicRepository.incrementTotal(topic1.getId());
        entityManager.clear();
        var topicInDb = topicRepository.findById(topic1.getId());
        assertThat(topicInDb).isNotEmpty();
        assertThat(topicInDb.get().getTotal()).isEqualTo(expectTotalSize5);
    }

    @Test
    void getAllTopicLiteDTOWhenEmptyList() {
        var actual = topicRepository.getAllTopicLiteDTO();
        assertThat(actual).isEmpty();
    }

    @Test
    void getAllTopicLiteWhenListTopicLiteDTO() {
        var dateNow = Calendar.getInstance();
        dateNow.set(Calendar.HOUR_OF_DAY, 0);
        var topic1 = Topic.of()
                .name("topic1")
                .text("text1")
                .created(dateNow)
                .updated(dateNow)
                .total(1)
                .position(1)
                .category(category1)
                .build();
        entityManager.persist(topic1);
        entityManager.clear();
        var topicLiteDto = new TopicLiteDTO(
                topic1.getId(), topic1.getName(),
                topic1.getText(), category1.getId(),
                category1.getName(), topic1.getPosition());
        var expectList = List.of(topicLiteDto);
        var actualList = topicRepository.getAllTopicLiteDTO();
        assertThat(actualList).isEqualTo(expectList);
    }

    @Test
    void getAllTopicLiteWhenListTwoTopicLiteDTO() {
        var dateNow = Calendar.getInstance();
        dateNow.set(Calendar.HOUR_OF_DAY, 0);
        var topic1 = Topic.of()
                .name("topic1")
                .text("text1")
                .created(dateNow)
                .updated(dateNow)
                .total(1)
                .position(1)
                .category(category1)
                .build();
        var topic2 = Topic.of()
                .name("topic2")
                .text("text2")
                .created(dateNow)
                .updated(dateNow)
                .total(1)
                .position(1)
                .category(category2)
                .build();
        entityManager.persist(topic1);
        entityManager.persist(topic2);
        entityManager.clear();
        var topicLiteDto1 = new TopicLiteDTO(
                topic1.getId(), topic1.getName(),
                topic1.getText(), category1.getId(),
                category1.getName(), topic1.getPosition());
        var topicLiteDto2 = new TopicLiteDTO(
                topic2.getId(), topic2.getName(),
                topic2.getText(), category2.getId(),
                category2.getName(), topic2.getPosition());
        var expectList = List.of(topicLiteDto1, topicLiteDto2);
        var actualList = topicRepository.getAllTopicLiteDTO();
        assertThat(actualList).isEqualTo(expectList);
    }

    @Test
    void getTopicLiteDtoByIdThenReturnOptionalEmpty() {
        var anyTopicId = -99;
        var actual = topicRepository.getTopicLiteDTOById(anyTopicId);
        assertThat(actual).isEmpty();
    }

    @Test
    void getTopicLiteDtoByIdThenReturnOptionalIsPresent() {
        var dateNow = Calendar.getInstance();
        dateNow.set(Calendar.HOUR_OF_DAY, 0);
        var topic1 = Topic.of()
                .name("topic1")
                .text("text1")
                .created(dateNow)
                .updated(dateNow)
                .total(1)
                .position(1)
                .category(category1)
                .build();
        entityManager.persist(topic1);
        entityManager.clear();
        var expectTopicLite = new TopicLiteDTO(
                topic1.getId(), topic1.getName(),
                topic1.getText(), category1.getId(),
                category1.getName(), topic1.getPosition());
        var actualTopicLiteOptional = topicRepository.getTopicLiteDTOById(topic1.getId());
        assertThat(actualTopicLiteOptional.isPresent()).isTrue();
        assertThat(actualTopicLiteOptional.get()).isEqualTo(expectTopicLite);
    }

    @Test
    void getTopicLiteDtoByIdAnyThenReturnOptionalEmpty() {
        var anyId = -99;
        var dateNow = Calendar.getInstance();
        dateNow.set(Calendar.HOUR_OF_DAY, 0);
        var topic1 = Topic.of()
                .name("topic1")
                .text("text1")
                .created(dateNow)
                .updated(dateNow)
                .total(1)
                .position(1)
                .category(category1)
                .build();
        entityManager.persist(topic1);
        entityManager.clear();
        var actualTopicLiteOptional = topicRepository.getTopicLiteDTOById(anyId);
        assertThat(actualTopicLiteOptional).isEmpty();
    }
}