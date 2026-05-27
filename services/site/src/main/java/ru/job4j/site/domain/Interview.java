package ru.job4j.site.domain;

import lombok.*;
import ru.job4j.site.enums.StatusInterview;

import java.sql.Timestamp;

/**
 * CheckDev пробное собеседование
 *
 * @author Dmitry Stepanov
 * @version 20.11.2023 21:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "of")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Interview {
    private int id;
    private int mode;
    private StatusInterview status;
    private int submitterId;
    private String title;
    private String additional;
    private String contactBy;
    private String approximateDate;
    private Timestamp createDate;
    private Integer topicId;
    private String author;
    private int agreedWisherId;
    private String cancelBy;
}