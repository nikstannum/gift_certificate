package ru.clevertec.ecl.service.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.ecl.data.entity.OrderInfo;
import ru.clevertec.ecl.service.dto.OrderInfoDto;

@Mapper
public interface OrderInfoMapper {
    OrderInfoDto toDto(OrderInfo entity);

    OrderInfo toModel(OrderInfoDto dto);
}
