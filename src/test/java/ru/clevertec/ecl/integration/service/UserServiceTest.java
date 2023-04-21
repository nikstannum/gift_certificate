package ru.clevertec.ecl.integration.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import ru.clevertec.ecl.integration.BaseIntegrationTest;
import ru.clevertec.ecl.service.dto.UserDto;
import ru.clevertec.ecl.service.dto.UserDto.UserRoleDto;
import ru.clevertec.ecl.service.exception.ClientException;
import ru.clevertec.ecl.service.exception.NotFoundException;
import ru.clevertec.ecl.service.impl.UserServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

public class UserServiceTest extends BaseIntegrationTest {

    @Autowired
    private UserServiceImpl service;
    @Autowired
    private EntityManager manager;


    @Test
    void checkFindByIdShouldReturnNotNull() {
        UserDto actual = service.findById(1L);
        assertThat(actual).isNotNull();
    }

    @Test
    void checkFindByIdShouldThrowNotFoundExc() {
        Assertions.assertThrows(NotFoundException.class, () -> service.findById(0L));
    }

    @Test
    void checkUpdateShouldReturnEquals() {
        UserDto expected = new UserDto();
        expected.setId(1L);
        expected.setFirstName("updated");
        expected.setLastName("updated");
        expected.setEmail("updated@google.com");
        expected.setPassword("updated");
        expected.setUserRoleDto(UserRoleDto.ADMIN);

        UserDto actual = service.update(expected);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkUpdateShouldThrowClientExc() {
        UserDto expected = new UserDto();
        expected.setId(1L);
        expected.setFirstName("updated");
        expected.setLastName("updated");
        expected.setEmail("ivanov@gmail.com");
        expected.setPassword("updated");
        expected.setUserRoleDto(UserRoleDto.ADMIN);

        Assertions.assertThrows(ClientException.class, () -> service.update(expected));
    }

    @Test
    void checkCreateShouldReturnIdNotNull() {
        UserDto userDto = new UserDto();
        userDto.setFirstName("created");
        userDto.setLastName("created");
        userDto.setEmail("created@google.com");
        userDto.setPassword("created");
        userDto.setUserRoleDto(UserRoleDto.USER);

        UserDto actual = service.create(userDto);

        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void checkCreateShouldThrowClientExc() {
        UserDto userDto = new UserDto();
        userDto.setFirstName("created");
        userDto.setLastName("created");
        userDto.setEmail("ivanov@gmail.com");
        userDto.setPassword("created");
        userDto.setUserRoleDto(UserRoleDto.ADMIN);

        Assertions.assertThrows(ClientException.class, () -> service.create(userDto));
    }

    @Test
    void checkDeleteShouldSuccess() {
        service.delete(1L);
        manager.flush();
    }

    @Test
    void checkDeleteShouldThrowNotFoundExc() {
        Assertions.assertThrows(NotFoundException.class, () -> service.delete(100L));
    }

    @Test
    void checkFindUserByEmailShouldReturnNotNull() {
        UserDto actual = service.findUserByEmail("ivanov@gmail.com");

        assertThat(actual).isNotNull();
    }

    @Test
    void checkFindUserByEmailShouldThrowNotFoundExc() {
        Assertions.assertThrows(NotFoundException.class, () -> service.findUserByEmail("qwerty"));
    }

    @Test
    void checkFindAllShouldReturn3() {
        int expectedSize = 3;
        Pageable pageable = PageRequest.of(0, 3, Direction.ASC, "id");
        Page<UserDto> actual = service.findAll(pageable);
        assertThat(actual).hasSize(expectedSize);
    }
}
