package ru.clevertec.ecl.service.exception;

import lombok.Getter;
import org.springframework.validation.Errors;

public class ValidationException extends ClevertecException {

    @Getter
    private final Errors errors;

    public ValidationException(Errors errors) {
        this.errors = errors;
    }
}
