package ru.checkdev.mock.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

/**
 * Конвертор для сохранения значения Enum StatusInterview в базу данных.
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 20.11.2023
 */
@Converter(autoApply = true)
public class StatusInterviewConverter implements AttributeConverter<StatusInterview, Integer> {

    /**
     * Если передаем null, то возвращаем значение ID статуса IS_UNKNOWN.
     *
     * @param statusInterview the entity attribute value to be converted
     * @return Integer ID Status
     */
    @Override
    public Integer convertToDatabaseColumn(StatusInterview statusInterview) {
        if (statusInterview == null) {
            return StatusInterview.IS_UNKNOWN.getId();
        }
        return statusInterview.getId();
    }

    /**
     * Если передаем null, то возвращаем статус IS_UNKNOWN
     *
     * @param statusId the data from the database column to be
     *                 converted
     * @return StatusInterview
     */
    @Override
    public StatusInterview convertToEntityAttribute(Integer statusId) {
        if (statusId == null) {
            return StatusInterview.IS_UNKNOWN;
        }
        return Stream.of(StatusInterview.values())
                .filter(s -> statusId.equals(s.getId()))
                .findFirst()
                .orElse(StatusInterview.IS_UNKNOWN);
    }
}
