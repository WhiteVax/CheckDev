package ru.job4j.site.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDTO {

    private int userId;
    private int categoryId;
    private int topicId;
    private int filterProfile;
    private int status;
    private int mode;
}
