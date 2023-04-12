package ru.clevertec.ecl.service.dto;

import lombok.Data;

@Data
public class QueryParamsDto {
    private String cert;
    private String tag;
    private String order;
    private String page;
    private String size;
}
