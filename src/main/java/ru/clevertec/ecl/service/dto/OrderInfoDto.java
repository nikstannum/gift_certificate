package ru.clevertec.ecl.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderInfoDto {
    private Long id;
    @JsonIgnore
    private OrderDto orderDto;
    private GiftCertificateDto giftCertificateDto;
    private Integer certificateQuantity;
    private BigDecimal certificatePrice;
}
