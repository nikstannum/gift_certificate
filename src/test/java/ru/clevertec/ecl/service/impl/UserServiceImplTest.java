package ru.clevertec.ecl.service.impl;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import ru.clevertec.ecl.data.entity.User;
import ru.clevertec.ecl.data.entity.User.UserRole;
import ru.clevertec.ecl.data.repository.UserRepository;
import ru.clevertec.ecl.service.dto.UserDto;
import ru.clevertec.ecl.service.dto.UserDto.UserRoleDto;
import ru.clevertec.ecl.service.exception.ClientException;
import ru.clevertec.ecl.service.exception.NotFoundException;
import ru.clevertec.ecl.service.mapper.Mapper;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    public static final String TEST_EMAIL = "test@test.com";
    public static final String TEST_PASSWORD = "test";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    @Mock
    private Mapper mapper;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
    }

    @Test
    void checkFindByIdShouldThrowNotFoundExc() {
        Mockito.doReturn(Optional.empty()).when(userRepository).findById(1L);
        Assertions.assertThrows(NotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void checkFindByIdShouldReturnEquals() {
        User user = getStandardUser(1L);
        UserDto dto = getStandardUserDto(1L);
        Mockito.doReturn(Optional.of(user)).when(userRepository).findById(1L);
        Mockito.doReturn(dto).when(mapper).convert(user);

        UserDto actual = service.findById(1L);

        assertThat(actual).isEqualTo(dto);
    }

    private UserDto getStandardUserDto(Long id) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setUserRoleDto(UserRoleDto.USER);
        dto.setEmail(TEST_EMAIL);
        dto.setPassword(TEST_PASSWORD);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        return dto;
    }

    private User getStandardUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setUserRole(UserRole.USER);
        user.setEmail(TEST_EMAIL);
        user.setPassword(TEST_PASSWORD);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        return user;
    }


    @Test
    void checkUpdateShouldThrowClientExc() {
        User user = getStandardUser(2L);
        Mockito.doReturn(user).when(userRepository).findUserByEmail(TEST_EMAIL);
        UserDto dto = getStandardUserDto(1L);

        Assertions.assertThrows(ClientException.class, () -> service.update(dto));
    }

    @Test
    void checkUpdateShouldReturnEquals() {
        User user = getStandardUser(1L);
        Mockito.doReturn(user).when(userRepository).findUserByEmail(TEST_EMAIL);
        Mockito.doReturn(user).when(userRepository).saveAndFlush(user);
        UserDto expected = getStandardUserDto(1L);
        Mockito.doReturn(user).when(mapper).convert(expected);
        Mockito.doReturn(expected).when(mapper).convert(user);

        UserDto actual = service.update(expected);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkCreateShouldThrowClientExc() {
        User user = getStandardUser(1L);
        Mockito.doReturn(user).when(userRepository).findUserByEmail(TEST_EMAIL);
        UserDto dto = getStandardUserDto(null);
        Assertions.assertThrows(ClientException.class, () -> service.create(dto));
    }

    @Test
    void checkCreateShouldReturnEquals() {
        Mockito.doReturn(null).when(userRepository).findUserByEmail(TEST_EMAIL);
        User user = getStandardUser(null);
        UserDto dto = getStandardUserDto(null);
        Mockito.doReturn(user).when(mapper).convert(dto);
        User created = getStandardUser(1L);
        Mockito.doReturn(created).when(userRepository).save(user);
        UserDto expected = getStandardUserDto(1L);
        Mockito.doReturn(expected).when(mapper).convert(created);

        UserDto actual = service.create(dto);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkDeleteShouldThrowNotFoundExc() {
        Mockito.doReturn(Optional.empty()).when(userRepository).findById(1L);
        Assertions.assertThrows(NotFoundException.class, () -> service.delete(1L));
    }

    @Captor
    ArgumentCaptor<User> captor;

    @Test
    void checkDeleteShouldCapture() {
        User user = getStandardUser(1L);
        user.setDeleted(false);
        Mockito.doReturn(Optional.of(user)).when(userRepository).findById(1L);

        service.delete(1L);
        Mockito.verify(userRepository).save(captor.capture());
        User actual = captor.getValue();

        assertThat(actual.isDeleted()).isEqualTo(true);
    }

    @Test
    void checkFindUserByEmailShouldThrowNotFoundExc() {
        Mockito.doReturn(null).when(userRepository).findUserByEmail(TEST_EMAIL);
        Assertions.assertThrows(NotFoundException.class, () -> service.findUserByEmail(TEST_EMAIL));
    }

    @Test
    void checkFindUserByEmailShouldReturnEquals() {
        User user = getStandardUser(1L);
        Mockito.doReturn(user).when(userRepository).findUserByEmail(TEST_EMAIL);
        UserDto expected = getStandardUserDto(1L);
        Mockito.doReturn(expected).when(mapper).convert(user);

        UserDto actual = service.findUserByEmail(TEST_EMAIL);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkFindAllShouldHasSize2() {
        Pageable pageable = PageRequest.of(0, 2, Direction.ASC, "id");
        List<User> list = List.of(new User(), new User());
        Page<User> userPage = new PageImpl<>(list);
        Mockito.doReturn(userPage).when(userRepository).findAll(pageable);

        Page<UserDto> page = service.findAll(pageable);

        assertThat(page).hasSize(2);
    }
}