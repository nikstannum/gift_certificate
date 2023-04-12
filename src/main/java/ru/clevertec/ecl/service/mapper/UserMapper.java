package ru.clevertec.ecl.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.ecl.data.entity.User;
import ru.clevertec.ecl.service.dto.UserDto;

@Mapper
public interface UserMapper {
    @Mapping(source = "userRole", target = "userRoleDto")
    UserDto toDto(User user);


    @Mapping(source = "userRoleDto", target = "userRole")
    User toModel(UserDto dto);
}
