package ru.checkdev.generator.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@Getter
public class Exam {

    private final Set<String> questions;

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
