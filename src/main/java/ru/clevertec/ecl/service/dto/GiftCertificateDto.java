package ru.clevertec.ecl.service.dto;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.List;
import lombok.Data;

@Data
public class GiftCertificateDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private Time lastUpdateDate;
    private Time createdDate;
    private List<TagDto> tags;
}
