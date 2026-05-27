package ru.job4j.site.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicLiteDTO {
    private int id;
    private String name;
    private String text;
    private int categoryId;
    private String categoryName;
    private int position;
}
