package ru.clevertec.ecl.service.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends ClientException {

    public NotFoundException(String code) {
        super(code);
    }

    public NotFoundException(String message, Throwable cause,String code) {
        super(message, cause, code);
    }

    public NotFoundException(String message, String code) {
        super(message, code);
    }

    public NotFoundException(Throwable cause, String code) {
        super(cause, code);
    }
}
