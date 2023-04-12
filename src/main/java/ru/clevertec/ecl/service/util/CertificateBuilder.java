package ru.clevertec.ecl.service.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.service.exception.ClientException;

/**
 * class that creates a certificate based on request parameters
 */
@Component
public class CertificateBuilder {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "descr";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_DURATION = "duration";
    public static final String CODE_BAD_REQUEST = "40000";
    public static final String EXC_MSG_INVALID_PRICE_DURATION = "Invalid value price or duration";
    public static final String COMMA = ",";
    public static final String COLON = ":";

    public GiftCertificate buildCertificate(QueryParams queryParams) {
        GiftCertificate certificate = new GiftCertificate();
        String params = queryParams.getCert();
        if (params != null) {
            String[] paramsArr = params.split(COMMA);
            for (String param : paramsArr) {
                setField(param, certificate);
            }
        }
        String tagsStr = queryParams.getTag();
        setTags(tagsStr, certificate);
        return certificate;
    }

    private void setTags(String tagsStr, GiftCertificate certificate) {
        if (tagsStr == null) {
            certificate.setTags(Collections.emptyList());
            return;
        }
        String[] tagsArr = tagsStr.split(COMMA);
        List<Tag> tags = new ArrayList<>();
        for (String tagParam : tagsArr) {
            String tagName = tagParam.split(COLON)[1];
            Tag tag = new Tag();
            tag.setName(tagName);
            tags.add(tag);
        }
        certificate.setTags(tags);
    }

    private void setField(String param, GiftCertificate certificate) {
        String[] fieldValueArr = param.split(COLON);
        String field = fieldValueArr[0];
        String value = fieldValueArr[1];
        try {
            switch (field) {
                case FIELD_NAME -> certificate.setName(value);
                case FIELD_DESCRIPTION -> certificate.setDescription(value);
                case FIELD_PRICE -> certificate.setPrice(BigDecimal.valueOf(Double.parseDouble(value)));
                case FIELD_DURATION -> certificate.setDuration(Integer.valueOf(value));
            }
        } catch (NumberFormatException e) {
            throw new ClientException(EXC_MSG_INVALID_PRICE_DURATION, e, CODE_BAD_REQUEST);
        }
    }

}
