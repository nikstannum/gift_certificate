package ru.clevertec.ecl.service.util.serializer;

import ru.clevertec.ecl.service.dto.GiftCertificateDto;

public interface CertificateSerializer {
    String serialize(Object dto);
}
