package ru.clevertec.ecl.data.entity;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.List;
import lombok.Data;

@Data
public class GiftCertificate {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private Time lastUpdateDate;
    private Time createdDate;
    private List<Tag> tags;
}
