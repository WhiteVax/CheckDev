package ru.checkdev.generator.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "_statistic")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VacancyStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private int count;
    @Column(name = "changes")
    private int countChanges;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VacancyStatistic that)) {
            return  false;
        }
        return id == that.id && count == that.count
                && countChanges == that.countChanges && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return ((Objects.hashCode(id) * 31 + name.hashCode()) * 31
                + Objects.hashCode(count)) * 31
                + Objects.hashCode(countChanges);
    }
}
