package ru.checkdev.desc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "of")
public class CategoryDTO {

    private int id;
    private String name;
    private int total;
    private long topicsSize;
    private int position;
}
