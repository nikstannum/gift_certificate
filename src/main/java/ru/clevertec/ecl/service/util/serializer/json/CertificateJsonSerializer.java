package ru.clevertec.ecl.service.util.serializer.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.text.SimpleDateFormat;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.service.exception.ClevertecException;
import ru.clevertec.ecl.service.util.serializer.CertificateSerializer;

@Component
public class CertificateJsonSerializer implements CertificateSerializer {
    public static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static final String MSG_PROCESS_ERROR = "Data processing error";

    @Override
    public String serialize(Object dto) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(df);
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(MSG_PROCESS_ERROR, e);
        }
    }
}
