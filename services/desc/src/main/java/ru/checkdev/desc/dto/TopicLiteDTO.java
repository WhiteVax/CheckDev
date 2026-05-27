package ru.checkdev.desc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO модель Topic
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 09.11.2023
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TopicLiteDTO {
    private int id;
    private String name;
    private String text;
    private int categoryId;
    private String categoryName;
    private int position;
}