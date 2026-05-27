package ru.job4j.site.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VacancyURL {

    private String vacancyLink;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VacancyURL that)) return false;
        return getVacancyLink().equals(that.getVacancyLink());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVacancyLink());
    }
}
