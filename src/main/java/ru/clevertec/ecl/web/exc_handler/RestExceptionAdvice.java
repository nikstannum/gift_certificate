package ru.clevertec.ecl.web.exc_handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.clevertec.ecl.service.dto.ErrorDto;
import ru.clevertec.ecl.service.exception.ClevertecException;
import ru.clevertec.ecl.service.exception.ClientException;
import ru.clevertec.ecl.service.exception.NotFoundException;
import ru.clevertec.ecl.service.util.serializer.Serializer;

@Log4j2
@RequiredArgsConstructor
@RestControllerAdvice("ru.clevertec.ecl")
public class RestExceptionAdvice {
    private static final String MSG_SERVER_ERROR = "Server error";
    private static final String MSG_CLIENT_ERROR = "Client error";
    private static final String DEFAULT_MSG = "Unknown error";
    private static final String CODE_DEFAULT = "50000";

    private final Serializer serializer;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String error(NotFoundException e) {
        log.error(e);
        ErrorDto dto = new ErrorDto(MSG_CLIENT_ERROR, e.getMessage(), e.getCode());
        return serializer.serialize(dto);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String error(ClientException e) {
        log.error(e);
        ErrorDto dto = new ErrorDto(MSG_CLIENT_ERROR, e.getMessage(), e.getCode());
        return serializer.serialize(dto);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String error(ClevertecException e) {
        log.error(e);
        ErrorDto dto = new ErrorDto(MSG_SERVER_ERROR, e.getMessage(), e.getCode());
        return serializer.serialize(dto);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String error(Exception e) {
        log.error(e);
        ErrorDto dto = new ErrorDto(MSG_SERVER_ERROR, DEFAULT_MSG, CODE_DEFAULT);
        return serializer.serialize(dto);
    }
}
