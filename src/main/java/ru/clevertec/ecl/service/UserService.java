package ru.clevertec.ecl.service;

import ru.clevertec.ecl.service.dto.UserDto;

public interface UserService extends CrudService<UserDto, Long> {
    UserDto findUserByEmail(String email);
}
