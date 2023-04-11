package ru.clevertec.ecl.service.dto;

import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ValidationResultDto extends ErrorDto {
    private static final String DEFAULT_ERROR_TYPE = "Validation Error";
    private static final String DEFAULT_ERROR_MESSAGE = "Sent data violates validation constraints";
    private static final String CODE = "42200";
    private Map<String, List<String>> messages;

    public ValidationResultDto() {
        super(DEFAULT_ERROR_TYPE, DEFAULT_ERROR_MESSAGE, CODE);
    }

    public ValidationResultDto(Map<String, List<String>> messages) {
        this();
        this.messages = messages;
    }
}
