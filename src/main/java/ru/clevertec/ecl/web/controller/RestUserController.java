package ru.clevertec.ecl.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.ecl.service.UserService;
import ru.clevertec.ecl.service.dto.UserDto;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class RestUserController {

    private final UserService userService;

    @ModelAttribute
    public UserDto userDto() {
        return new UserDto();
    }

    @ResponseBody
    @GetMapping()
    public Page<UserDto> findAll(Pageable pageable) {
        return userService.findAll(pageable);
    }
    @ResponseBody
    @GetMapping("/{id}")
    public UserDto findById(@PathVariable Long id) {
        return userService.findById(id);
    }



}
