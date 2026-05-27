package ru.job4j.site.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "of")
public class InterviewDTO {
    private int id;
    private int mode;
    private int statusId;
    private String statusInfo;
    private int submitterId;
    /**
     * поле выбранного автором wisher
     * Arcady555
     */
    private int agreedWisherId;
    private String title;
    private String additional;
    private String contactBy;
    private String approximateDate;
    private String createDate;
    private int topicId;
    /**
     * поле автор собеседования (тот кто создал собеседование)
     */
    private String author;
    /**
     * поле количество откликов(желающих участвовать в интервью)
     */
    private Long countWishers;
    /**
     * поле причины отмены собеседования
     */
    private String cancelBy;
}
