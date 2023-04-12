package ru.clevertec.ecl.service.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.service.dto.GiftCertificateDto;

@Mapper(uses = TagMapper.class)
public interface GiftCertificateMapper {

    GiftCertificateDto toDto(GiftCertificate giftCertificate);

    GiftCertificate toModel(GiftCertificateDto dto);
}
