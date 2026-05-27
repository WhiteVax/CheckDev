package ru.checkdev.generator.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "last_update_time")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LastStatisticUpdateDateTime {

    @Id
    private int id;

    @Column(name = "_time")
    private LocalDateTime time;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LastStatisticUpdateDateTime that)) {
            return false;
        }
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
