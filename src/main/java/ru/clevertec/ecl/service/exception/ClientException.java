package ru.clevertec.ecl.service.exception;

import lombok.Getter;

/**
 * class of client-side exceptions handled in the application
 */
@Getter
public class ClientException extends ClevertecException {

    public ClientException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public ClientException(String message, String code) {
        super(message, code);
    }
}
