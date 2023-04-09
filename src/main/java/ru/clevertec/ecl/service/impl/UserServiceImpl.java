package ru.clevertec.ecl.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import ru.clevertec.ecl.data.entity.User;
import ru.clevertec.ecl.data.repository.UserRepository;
import ru.clevertec.ecl.service.UserService;
import ru.clevertec.ecl.service.dto.UserDto;
import ru.clevertec.ecl.service.exception.ClientException;
import ru.clevertec.ecl.service.exception.NotFoundException;
import ru.clevertec.ecl.service.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public static final String EXC_MSG_NOT_FOUND_USER_ID = "couldn't found user with id = ";
    public static final String CODE_USER_READ = "40432";
    public static final String CODE_USER_DELETE = "40434";
    public static final String EXC_MSG_NOT_FOUND_USER_EMAIL = "couldn't found user by email = ";
    public static final String COLUMN_ID = "id";
    public static final String CODE_USER_CREATE = "40031";
    public static final String EXC_MSG_USER_EMAIL_EXISTS = "already registered user with email ";
    public static final String CODE_USER_UPD = "40433";
    private final Mapper mapper;
    private final UserRepository userRepository;

    /**
     * returns the user by the given id
     *
     * @param id object identifier
     * @return the requested object
     */
    @Override
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(EXC_MSG_NOT_FOUND_USER_ID + id, CODE_USER_READ));
        return mapper.convert(user);
    }

    private void validateUpdate(UserDto dto) {
        User existing = userRepository.findUserByEmail(dto.getEmail());
        if (existing != null && !existing.getId().equals(dto.getId())) {
            throw new NotFoundException(EXC_MSG_NOT_FOUND_USER_ID + dto.getId(), CODE_USER_UPD);
        }
    }

    /**
     * updates an existing user. If the given email already exists, an exception will be thrown
     *
     * @param dto object for updating
     * @return the updated object
     */
    @Override
    public UserDto update(UserDto dto) {
        validateUpdate(dto);
        User user = mapper.convert(dto);
        User updated = userRepository.saveAndFlush(user);
        return mapper.convert(updated);
    }

    private void validateCreate(UserDto userDto) {
        User existing = userRepository.findUserByEmail(userDto.getEmail());
        if (existing != null) {
            throw new ClientException(EXC_MSG_USER_EMAIL_EXISTS + userDto.getEmail(), CODE_USER_CREATE);
        }
    }

    /**
     * creates a new user. If a user with this email already exists, an exception will be thrown
     *
     * @param dto object for creation
     * @return created object
     */
    @Override
    public UserDto create(UserDto dto) {
        validateCreate(dto);
        User user = mapper.convert(dto);
        User updated = userRepository.save(user);
        return mapper.convert(updated);
    }

    /**
     * delete user by id. If there is no user with this id then an exception will be thrown
     *
     * @param id object identifier
     */
    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(EXC_MSG_NOT_FOUND_USER_ID + id, CODE_USER_DELETE));
        user.setDeleted(true);
        userRepository.save(user);
    }

    /**
     * return user by email
     *
     * @param email user email
     * @return the requested object
     */
    @Override
    public UserDto findUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new NotFoundException(EXC_MSG_NOT_FOUND_USER_EMAIL + email, CODE_USER_READ);
        }
        return mapper.convert(user);
    }

    /**
     * returns a paginated list of all users
     *
     * @param pageable abstract interface for pagination information
     * @return page of objects
     */
    @Override
    public Page<UserDto> findAll(Pageable pageable) {
        Sort sort = pageable.getSort();
        if (!sort.isSorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Direction.ASC, COLUMN_ID);
        }
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(mapper::convert);
    }
}
