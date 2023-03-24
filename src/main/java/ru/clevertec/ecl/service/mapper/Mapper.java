package ru.clevertec.ecl.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.service.dto.GiftCertificateDto;
import ru.clevertec.ecl.service.dto.QueryParamsDto;
import ru.clevertec.ecl.service.dto.TagDto;

@Component
@RequiredArgsConstructor
public class Mapper {
    private final TagMapper tagMapper;
    private final GiftCertificateMapper giftCertificateMapper;
    private final QueryMapper queryMapper;

    public Tag convert(TagDto dto) {
        return tagMapper.toModel(dto);
    }

    public TagDto convert(Tag entity) {
        return tagMapper.toDto(entity);
    }

    public GiftCertificate convert(GiftCertificateDto dto) {
        return giftCertificateMapper.toModel(dto);
    }

    public GiftCertificateDto convert(GiftCertificate entity) {
        return giftCertificateMapper.toDto(entity);
    }

    public QueryParams convert(QueryParamsDto dto) {
        return queryMapper.toModel(dto);
    }

    public QueryParamsDto convert(QueryParams entity) {
        return queryMapper.toDto(entity);
    }

}
