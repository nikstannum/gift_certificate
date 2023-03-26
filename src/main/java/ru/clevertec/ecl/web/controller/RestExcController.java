package ru.clevertec.ecl.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.ecl.service.dto.ErrorDto;
import ru.clevertec.ecl.service.util.serializer.Serializer;

@RestController
@RequiredArgsConstructor
public class RestExcController {
    public static final String SERVER_ERROR = "Server error";
    public static final String DEFAULT_MSG = "Something went wrong";
    public static final String CODE_DEFAULT = "50000";
    private final Serializer serializer;


    @RequestMapping("/error")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleError() {
        ErrorDto errorDto = new ErrorDto(SERVER_ERROR, DEFAULT_MSG, CODE_DEFAULT);
        return serializer.serialize(errorDto);
    }
}
