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

@Log4j2
@RequiredArgsConstructor
@RestControllerAdvice("ru.clevertec.ecl")
public class RestExceptionAdvice {
    private static final String MSG_SERVER_ERROR = "Server error";
    private static final String MSG_CLIENT_ERROR = "Client error";
    private static final String DEFAULT_MSG = "Unknown error";
    private static final String CODE_DEFAULT = "50000";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto error(NotFoundException e) {
        log.error(e);
        return new ErrorDto(MSG_CLIENT_ERROR, e.getMessage(), e.getCode());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto error(ClientException e) {
        log.error(e);
        return new ErrorDto(MSG_CLIENT_ERROR, e.getMessage(), e.getCode());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto error(ClevertecException e) {
        log.error(e);
        return new ErrorDto(MSG_SERVER_ERROR, e.getMessage(), e.getCode());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto error(Exception e) {
        log.error(e);
        return new ErrorDto(MSG_SERVER_ERROR, DEFAULT_MSG, CODE_DEFAULT);
    }
}
