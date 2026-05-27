package ru.job4j.site.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class VacancyStatistic {

    private int id;
    private String name;
    private int count;
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
