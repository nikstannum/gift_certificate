package ru.clevertec.ecl.data.entity.converter;

import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.data.entity.User.UserRole;

/**
 * converter class that converts enum value to identifier and vice versa
 */
@Component
public class UserRoleConverter implements AttributeConverter<UserRole, Long> {
    @Override
    public Long convertToDatabaseColumn(UserRole attribute) {
        switch (attribute) {
            case ADMIN -> {
                return 1L;
            }
            case MANAGER -> {
                return 2L;
            }
            case USER -> {
                return 3L;
            }
            default -> throw new IllegalArgumentException(attribute + " not supported");
        }
    }

    @Override
    public UserRole convertToEntityAttribute(Long dbData) {
        if (dbData > Integer.MAX_VALUE) {
            throw new RuntimeException(dbData + " not supported");
        }
        int intValue = dbData.intValue();
        switch (intValue) {
            case 1 -> {
                return UserRole.ADMIN;
            }
            case 2 -> {

                return UserRole.MANAGER;
            }
            case 3 -> {
                return UserRole.USER;
            }
            default -> throw new IllegalArgumentException(dbData + " not supported");
        }
    }
}
