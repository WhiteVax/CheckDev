package ru.job4j.site.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InnerMessageDTO {

    private int id;
    private int userId;
    private String text;
    private Timestamp created;
    private int interviewId;
}
