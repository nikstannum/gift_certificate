package ru.clevertec.ecl.service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.Data;

@Data
public class GiftCertificateDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private Instant lastUpdateDate;
    private Instant createDate;
    private List<TagDto> tags;
}


