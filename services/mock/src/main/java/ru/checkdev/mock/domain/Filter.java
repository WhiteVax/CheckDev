package ru.checkdev.mock.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity(name = "cd_filter")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Filter {

    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;
    @Column(name = "category_id")
    private int categoryId;
    @Column(name = "topic_id")
    private int topicId;
    @Column(name = "filter_profile")
    private int filterProfile;
    @Column(name = "status")
    private int status;
    @Column(name = "_mode")
    private int mode;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Filter filter)) {
            return false;
        }
        return userId == filter.userId
                && categoryId == filter.categoryId
                && topicId == filter.topicId
                && status == filter.status;
    }

    @Override
    public int hashCode() {
        return ((((((Objects.hash(userId)) * 31)
                + Objects.hash(categoryId)) * 31) + Objects.hash(topicId)) * 31)
                + Objects.hash(status) * 31;
    }
}
