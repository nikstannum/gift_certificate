package ru.clevertec.ecl.data.repository.impl;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.clevertec.ecl.TestConfig;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.data.repository.GiftCertificateRepository;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringJUnitConfig(TestConfig.class)
class GiftCertificateRepositoryImplTest {

    public static final String CERT_NAME_NOT_EXISTS = "cert name";
    public static final String CERT_DESCRIPTION_NOT_EXISTS = "description";
    public static final String TAG_NAME_NOT_EXISTS = "tag name";
    public static final String CERT_NAME_EXISTS_SKYDIVING = "skydiving";
    public static final String DESCR_LIKE_SHOP = "descr:like:shop";
    public static final String DESCR_LIKE_QWERTY = "descr:like:qwerty";
    public static final String NAME_UPDATED = "updated";

    private final GiftCertificateRepository repository;

    @Autowired
    public GiftCertificateRepositoryImplTest(GiftCertificateRepository repository) {
        this.repository = repository;
    }

    @Test
    void checkFindByIdShouldEquals() {
        GiftCertificate certificate = repository.findById(1L);
        assertThat(certificate.getName()).isEqualTo(CERT_NAME_EXISTS_SKYDIVING);
    }

    @Test
    void checkFindByParamsShouldReturnNotEmpty() {
        QueryParams queryParams = new QueryParams();
        queryParams.setCert(DESCR_LIKE_SHOP);

        List<GiftCertificate> actual = repository.findByParams(queryParams);

        assertThat(actual).isNotEmpty();
    }

    @Test
    void checkFindByParamsShouldReturnEmpty() {
        QueryParams queryParams = new QueryParams();
        queryParams.setCert(DESCR_LIKE_QWERTY);

        List<GiftCertificate> actual = repository.findByParams(queryParams);

        assertThat(actual).isEmpty();
    }

    @Test
    void checkUpdateShouldReturnEquals() {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(1L);
        certificate.setName(NAME_UPDATED);

        repository.update(certificate);

        assertThat(certificate.getName()).isEqualTo(NAME_UPDATED);
    }

    @Test
    void checkCreateShouldIdNotNull() {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setName(CERT_NAME_NOT_EXISTS);
        certificate.setDescription(CERT_DESCRIPTION_NOT_EXISTS);
        Tag tag1 = new Tag();
        tag1.setName(TAG_NAME_NOT_EXISTS);
        certificate.setTags(List.of(tag1));

        repository.create(certificate);

        assertThat(certificate.getId()).isNotNull();
    }
}
