package ru.clevertec.ecl.service.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.service.dto.TagDto;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagDto toDto(Tag tag);

    Tag toModel(TagDto dto);
}
