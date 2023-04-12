package ru.clevertec.ecl.data.repository;

import ru.clevertec.ecl.data.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {
    /**
     * method to find a user by email
     *
     * @param email user email
     * @return the user with the specified email
     */
    User findUserByEmail(String email);
}
