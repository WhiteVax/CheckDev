package ru.job4j.site.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryWithTopicDTO {

    private int categoryId;
    private String categoryName;
    private int topicId;
    private String topicName;
    private int interviewId;
    private int submitterId;
}
