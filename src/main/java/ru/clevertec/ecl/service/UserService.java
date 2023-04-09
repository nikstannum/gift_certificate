package ru.clevertec.ecl.service;

import ru.clevertec.ecl.service.dto.UserDto;

public interface UserService extends CrudService<UserDto, Long> {
    /**
     * method returning user from repository by email
     *
     * @param email user email
     * @return searched user
     */
    UserDto findUserByEmail(String email);
}
