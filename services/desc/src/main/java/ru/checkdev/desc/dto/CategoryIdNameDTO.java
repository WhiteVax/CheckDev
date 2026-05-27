package ru.checkdev.desc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CheckDev пробное собеседование
 *
 * @author Dmitry Stepanov
 * @version 24.11.2023 23:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryIdNameDTO {

    private int id;
    private String name;
}
