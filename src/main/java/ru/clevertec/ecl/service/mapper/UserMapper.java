package ru.clevertec.ecl.service.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.ecl.data.entity.User;
import ru.clevertec.ecl.service.dto.UserDto;

@Mapper
public interface UserMapper {

    UserDto toDto(User user);

    User toModel(UserDto dto);
}
