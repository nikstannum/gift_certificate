package ru.clevertec.ecl.data.repository;

import ru.clevertec.ecl.data.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {
    User findUserByEmail(String email);
}
