package ru.clevertec.ecl.service.exception;

import lombok.Getter;

@Getter
public class ClientException extends ClevertecException {

    public ClientException(String code) {
        super(code);
    }

    public ClientException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public ClientException(String message, String code) {
        super(message,  code);
    }

    public ClientException(Throwable cause, String code) {
        super(cause, code);
    }
}
