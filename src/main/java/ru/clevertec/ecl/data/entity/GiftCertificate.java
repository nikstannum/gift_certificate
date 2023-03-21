package ru.clevertec.ecl.data.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class GiftCertificate {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer duration;
//    @Setter(AccessLevel.PACKAGE) // TODO package structure
    private LocalDateTime lastUpdateDate;
//    @Setter(AccessLevel.PACKAGE) //TODO package structure
    private LocalDateTime createdDate;
    private List<Tag> tags;
}
