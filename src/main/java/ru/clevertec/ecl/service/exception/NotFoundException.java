package ru.clevertec.ecl.service.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends ClientException {

    public NotFoundException(String message, String code) {
        super(message, code);
    }
}
