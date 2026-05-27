package ru.job4j.site.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class Exam {

    private Set<String> questions;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Exam exam)) {
            return false;
        }
        return questions.equals(exam.questions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questions);
    }
}
