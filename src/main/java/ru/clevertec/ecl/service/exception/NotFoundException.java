package ru.clevertec.ecl.service.exception;

import lombok.Getter;

/**
 * the class of the client exception associated with the impossibility of obtaining a resource
 */
@Getter
public class NotFoundException extends ClientException {

    public NotFoundException(String message, String code) {
        super(message, code);
    }
}
