package ru.clevertec.ecl.service.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.service.dto.QueryParamsDto;

@Mapper(componentModel = "spring")
public interface QueryMapper {

    QueryParamsDto toDto(QueryParams queryParams);

    QueryParams toModel(QueryParamsDto dto);
}
