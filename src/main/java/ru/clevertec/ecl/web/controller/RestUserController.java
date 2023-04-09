package ru.clevertec.ecl.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.ecl.service.OrderService;
import ru.clevertec.ecl.service.UserService;
import ru.clevertec.ecl.service.dto.OrderDto;
import ru.clevertec.ecl.service.dto.UserDto;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class RestUserController {

    private final UserService userService;
    private final OrderService orderService;


    @ResponseBody
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<UserDto> findAll(Pageable pageable) {
        return userService.findAll(pageable);
    }

    @ResponseBody
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @ResponseBody
    @GetMapping("/{id}/orders")
    @ResponseStatus(HttpStatus.OK)
    public Page<OrderDto> findOrdersByUserId(@PathVariable Long id, Pageable pageable) {
        return orderService.findOrdersByUserId(id, pageable);
    }

    @ResponseBody
    @GetMapping("/{userId}/orders/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto findOrderById(@PathVariable Long userId, @PathVariable Long orderId) {
        return orderService.findOrderByIdByUserId(userId, orderId);
    }
}
