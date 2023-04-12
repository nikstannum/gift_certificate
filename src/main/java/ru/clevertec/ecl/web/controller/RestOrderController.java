package ru.clevertec.ecl.web.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.clevertec.ecl.service.OrderService;
import ru.clevertec.ecl.service.dto.GiftCertificateDto;
import ru.clevertec.ecl.service.dto.OrderCreateDto;
import ru.clevertec.ecl.service.dto.OrderCreateDto.Item;
import ru.clevertec.ecl.service.dto.OrderDto;
import ru.clevertec.ecl.service.dto.OrderInfoDto;
import ru.clevertec.ecl.service.dto.UserDto;
import ru.clevertec.ecl.service.exception.ValidationException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/orders")
public class RestOrderController {

    private final OrderService orderService;

    @ModelAttribute
    OrderCreateDto orderCreateDto() {
        return new OrderCreateDto();
    }

    /**
     * endpoint to create order based on JSON string
     * <p>
     * JSON string example:
     * {
     * "userId":1,
     * "items":[{"certId":1, "quantity":2}]
     * }
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OrderDto> create(@RequestBody @Valid OrderCreateDto orderCreateDto, Errors errors) {
        checkErrors(errors);
        OrderDto orderDto = processParams(orderCreateDto);
        OrderDto created = orderService.create(orderDto);
        return buildResponseCreated(created);
    }

    private void checkErrors(Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
    }

    private ResponseEntity<OrderDto> buildResponseCreated(OrderDto created) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(getLocation(created))
                .body(created);
    }

    private URI getLocation(OrderDto created) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("api/users/{id}/orders/{id}")
                .buildAndExpand(created.getUserDto().getId(), created.getId())
                .toUri();
    }

    private OrderDto processParams(OrderCreateDto orderCreateDto) {
        OrderDto orderDto = new OrderDto();
        UserDto userDto = new UserDto();
        Long userId = orderCreateDto.getUserId();
        userDto.setId(userId);
        orderDto.setUserDto(userDto);
        List<OrderInfoDto> infoDtos = new ArrayList<>();
        List<Item> items = orderCreateDto.getItems();
        for (Item item : items) {
            OrderInfoDto infoDto = new OrderInfoDto();
            GiftCertificateDto certificateDto = new GiftCertificateDto();
            certificateDto.setId(item.getCertId());
            infoDto.setGiftCertificateDto(certificateDto);
            infoDto.setCertificateQuantity(item.getQuantity());
            infoDtos.add(infoDto);
        }
        orderDto.setDetailsDto(infoDtos);
        return orderDto;
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Page<OrderDto> findAll(Pageable pageable) {
        return orderService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public OrderDto findById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }
}
