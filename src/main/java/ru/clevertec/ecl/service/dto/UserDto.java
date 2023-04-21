package ru.clevertec.ecl.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    @JsonIgnore
    private String password;

    private UserRoleDto userRoleDto;


    public enum UserRoleDto {
        ADMIN, MANAGER, USER
    }
}
