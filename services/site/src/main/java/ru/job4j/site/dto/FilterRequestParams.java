package ru.job4j.site.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FilterRequestParams {

    private List<Integer> topicIds;
    private int submitterId;
    private int wisherId;
    private int agreedWisherId;
    private int status;
    private int mode;
    private boolean exclude;
}