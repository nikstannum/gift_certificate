package ru.clevertec.ecl.data.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
public class GiftCertificate {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private LocalDateTime lastUpdateDate;
    private LocalDateTime createdDate;
    private List<Tag> tags;
}
