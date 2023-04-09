package ru.clevertec.ecl.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.clevertec.ecl.data.entity.OrderInfo;
import ru.clevertec.ecl.service.dto.OrderInfoDto;

@Mapper(uses = GiftCertificateMapper.class)
public interface OrderInfoMapper {

    @Mappings({@Mapping(source = "giftCertificate", target = "giftCertificateDto"),
            @Mapping(source = "order", target = "orderDto")})
    OrderInfoDto toDto(OrderInfo entity);

    @Mappings({@Mapping(source = "giftCertificateDto", target = "giftCertificate"),
            @Mapping(source = "orderDto", target = "order")})
    OrderInfo toModel(OrderInfoDto dto);
}
