package ru.clevertec.ecl.service.exception;

public class ClevertecException extends RuntimeException {

    public ClevertecException() {
    }

    public ClevertecException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClevertecException(String message) {
        super(message);
    }

    public ClevertecException(Throwable cause) {
        super(cause);
    }
}
