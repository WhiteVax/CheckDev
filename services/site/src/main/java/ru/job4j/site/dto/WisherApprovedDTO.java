package ru.job4j.site.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WisherApprovedDTO {

    private int interviewId;
    private int wisherId;
    private int wisherUserId;
    private String interviewTitle;
    private String interviewLink;
    private String contactBy;
}
