package ru.clevertec.ecl.service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Data;

@Data
public class OrderCreateDto {
    @Positive(message = "user ID must be greater than 0")
    private Long userId;
    private List<@Valid Item> items;

    @Data
    public static class Item {
        @Positive(message = "certificate ID must be greater than 0")
        Long certId;
        @Min(value = 1, message = "quantity must be greater than 0")
        @NotNull(message = "specify the number of certificates")
        Integer quantity;
    }
}
