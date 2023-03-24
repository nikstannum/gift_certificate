package ru.clevertec.ecl.data.entity;

import lombok.Data;

@Data
public class QueryParams {
    private String cert;
    private String tag;
    private String order;
    private String page;
    private String size;
}
