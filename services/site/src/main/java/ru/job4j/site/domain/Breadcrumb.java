package ru.job4j.site.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Breadcrumb {

    private String name;
    private String url;

}