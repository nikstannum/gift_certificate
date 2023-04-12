package ru.clevertec.ecl.service.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.ecl.data.entity.User.UserRole;
import ru.clevertec.ecl.service.dto.UserDto.UserRoleDto;

@Mapper
public interface RoleMapper {

    UserRole toModel(UserRoleDto dto);

    UserRoleDto toDto(UserRole entity);
}
