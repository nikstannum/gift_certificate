package ru.clevertec.ecl.service.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private UserDto userDto;
    private BigDecimal totalCost;
    private StatusDto statusDto;
    private List<OrderInfoDto> detailsDto;

    public enum StatusDto {
        PENDING, PAID, DELIVERED, CANCELED
    }
}
