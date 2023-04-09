package ru.clevertec.ecl.service.exception;

import lombok.Getter;

/**
 * the main class of exceptions handled in the application
 */
@Getter
public class ClevertecException extends RuntimeException {

    private final String code;

    public ClevertecException(String message, Throwable cause, String code) {
        super(message, cause);
        this.code = code;
    }

    public ClevertecException(String message, String code) {
        super(message);
        this.code = code;
    }
}
