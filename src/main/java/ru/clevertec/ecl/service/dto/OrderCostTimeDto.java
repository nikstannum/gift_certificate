package ru.clevertec.ecl.service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;

@Data
public class OrderCostTimeDto {
    private BigDecimal totalCost;
    private Instant purchaseTime;
}
