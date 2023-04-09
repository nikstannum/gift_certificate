package ru.clevertec.ecl.service.dto;

import java.util.List;
import lombok.Data;

@Data
public class OrderCreateDto {
    private Long userId;
    List<Item> items;

    @Data
    public static class Item {
        Long certId;
        Integer quantity;
    }
}
