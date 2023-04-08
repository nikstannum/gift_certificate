package ru.clevertec.ecl.service.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.ecl.data.entity.Order;
import ru.clevertec.ecl.service.dto.OrderDto;

@Mapper(uses = OrderInfoMapper.class)
public interface OrderMapper {
    OrderDto toDto(Order order);

    Order toModel(OrderDto dto);
}
