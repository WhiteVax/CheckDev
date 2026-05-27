package ru.checkdev.mock.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * StatusInterviewConverter TEST
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 20.11.2023
 */
class StatusInterviewConverterTest {

    @Test
    void whenConvertToDatabaseColumnNullThenIdIsUnknown() {
        StatusInterview expect = StatusInterview.IS_UNKNOWN;
        StatusInterviewConverter converter = new StatusInterviewConverter();
        int actualId = converter.convertToDatabaseColumn(null);
        assertThat(actualId).isEqualTo(expect.getId());
    }

    @Test
    void whenConvertToDatabaseColumnIsUnknownThenIdIsUnknown() {
        StatusInterview expect = StatusInterview.IS_UNKNOWN;
        StatusInterviewConverter converter = new StatusInterviewConverter();
        int actualId = converter.convertToDatabaseColumn(expect);
        assertThat(actualId).isEqualTo(expect.getId());
    }

    @Test
    void whenConvertToDatabaseColumnIsNewThenIdIsNew() {
        StatusInterview expect = StatusInterview.IS_NEW;
        StatusInterviewConverter converter = new StatusInterviewConverter();
        int actualId = converter.convertToDatabaseColumn(expect);
        assertThat(actualId).isEqualTo(expect.getId());
    }

    @Test
    void whenConvertToDatabaseColumnInProgressThenIdInProgress() {
        StatusInterview expect = StatusInterview.IN_PROGRESS;
        StatusInterviewConverter converter = new StatusInterviewConverter();
        int actualId = converter.convertToDatabaseColumn(expect);
        assertThat(actualId).isEqualTo(expect.getId());
    }

    @Test
    void whenConvertToDatabaseColumnIsFeedbackThenIdIsFeedback() {
        StatusInterview expect = StatusInterview.IS_FEEDBACK;
        StatusInterviewConverter converter = new StatusInterviewConverter();
        int actualId = converter.convertToDatabaseColumn(expect);
        assertThat(actualId).isEqualTo(expect.getId());
    }

    @Test
    void whenConvertToDatabaseColumnIsCompletedThenIdIsCompleted() {
        StatusInterview expect = StatusInterview.IS_COMPLETED;
        StatusInterviewConverter converter = new StatusInterviewConverter();
        int actualId = converter.convertToDatabaseColumn(expect);
        assertThat(actualId).isEqualTo(expect.getId());
    }

    @Test
    void whenConvertToDatabaseColumnIsCanceledThenIdIsCanceled() {
        StatusInterview expect = StatusInterview.IS_CANCELED;
        StatusInterviewConverter converter = new StatusInterviewConverter();
        int actualId = converter.convertToDatabaseColumn(expect);
        assertThat(actualId).isEqualTo(expect.getId());
    }

    @Test
    void whenConvertToEntityAttributeNullThenIsUnknown() {
        StatusInterview expect = StatusInterview.IS_UNKNOWN;
        StatusInterviewConverter converter = new StatusInterviewConverter();
        StatusInterview actual = converter.convertToEntityAttribute(null);
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void whenConvertToEntityAttributeIsUnknownThenIsUnknown() {
        StatusInterview expect = StatusInterview.IS_UNKNOWN;
        StatusInterviewConverter converter = new StatusInterviewConverter();
        StatusInterview actual = converter.convertToEntityAttribute(expect.getId());
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void whenConvertToEntityAttributeIdIsNEWThenIsNEW() {
        StatusInterview expect = StatusInterview.IS_NEW;
        StatusInterviewConverter converter = new StatusInterviewConverter();
        StatusInterview actual = converter.convertToEntityAttribute(expect.getId());
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void whenConvertToEntityAttributeIdInPROGRESSThenInPROGRESS() {
        StatusInterview expect = StatusInterview.IN_PROGRESS;
        StatusInterviewConverter converter = new StatusInterviewConverter();
        StatusInterview actual = converter.convertToEntityAttribute(expect.getId());
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void whenConvertToEntityAttributeIdIsFeedbackThenIsFeedback() {
        StatusInterview expect = StatusInterview.IS_FEEDBACK;
        StatusInterviewConverter converter = new StatusInterviewConverter();
        StatusInterview actual = converter.convertToEntityAttribute(expect.getId());
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void whenConvertToEntityAttributeIdCompletedThenIsCompleted() {
        StatusInterview expect = StatusInterview.IS_COMPLETED;
        StatusInterviewConverter converter = new StatusInterviewConverter();
        StatusInterview actual = converter.convertToEntityAttribute(expect.getId());
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void whenConvertToEntityAttributeIdIsCanceledThenIsCanceled() {
        StatusInterview expect = StatusInterview.IS_CANCELED;
        StatusInterviewConverter converter = new StatusInterviewConverter();
        StatusInterview actual = converter.convertToEntityAttribute(expect.getId());
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void whenConvertToEntityAttributeIdNotFoundThenIsUnknown() {
        Integer notFoundId = -99;
        StatusInterview expect = StatusInterview.IS_UNKNOWN;
        StatusInterviewConverter converter = new StatusInterviewConverter();
        StatusInterview actual = converter.convertToEntityAttribute(notFoundId);
        assertThat(actual).isEqualTo(expect);
    }
}