package ru.clevertec.ecl.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.clevertec.ecl.data.entity.Order;
import ru.clevertec.ecl.service.dto.OrderDto;

@Mapper(uses = {OrderInfoMapper.class, UserMapper.class, OrderInfoMapper.class})
public interface OrderMapper {

    @Mappings({@Mapping(source = "user", target = "userDto"),
            @Mapping(source = "status", target = "statusDto"),
            @Mapping(source = "details", target = "detailsDto")})
    OrderDto toDto(Order order);

    @Mappings({@Mapping(source = "userDto", target = "user"),
            @Mapping(source = "statusDto", target = "status"),
            @Mapping(source = "detailsDto", target = "details")})
    Order toModel(OrderDto dto);
}
