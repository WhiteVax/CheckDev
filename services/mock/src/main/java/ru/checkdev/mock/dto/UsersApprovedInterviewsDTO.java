package ru.checkdev.mock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO модель описывает пользователя(по id) и количество его проведенных собеседования
 *
 * @author Rustam Saidov, user Rustam Saidov
 * @since 05.12.2023
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "of")
public class UsersApprovedInterviewsDTO {
    private int userId;
    private long approvedInterviews;
}
