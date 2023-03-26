package ru.clevertec.ecl.data.repository.dao.impl;

import java.math.BigDecimal;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.repository.dao.GiftCertificateDao;
import ru.clevertec.ecl.service.exception.ClevertecException;
import ru.clevertec.ecl.service.exception.ClientException;
import ru.clevertec.ecl.service.exception.NotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GiftCertificateDaoImplTest {

    private GiftCertificateDao dao;

    @BeforeEach
    void setUp() {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:sql/schema.sql")
                .addScript("classpath:sql/data.sql")
                .build();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        dao = new GiftCertificateDaoImpl(template);
    }

    @ParameterizedTest
    @CsvSource({"1,1", "-1,5", "-5,1", "-1,-1", "0,0"})
    void checkCreateCertificateTagEntryShouldThrowClientExc(Long certId, Long tagId) {
        assertThrows(ClientException.class, () -> dao.createCertificateTagEntry(certId, tagId));
    }

    @Test
    void checkCreateCertificateTagEntryShouldSuccess() {
        dao = Mockito.mock(GiftCertificateDao.class);
        dao.createCertificateTagEntry(1L, 5L);
        Mockito.verify(dao).createCertificateTagEntry(1L, 5L);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 200})
    void checkFindByIdShouldThrowNotFoundExc(long id) {
        assertThrows(NotFoundException.class, () -> dao.findById(id));
    }

    @Test
    void checkFindByIdShouldEquals() {
        String expectedName = "skydiving";
        String actualName = dao.findById(1L).getName();
        assertThat(actualName).isEqualTo(expectedName);
    }

    @Test
    void checkUpdateShouldEquals() {
        GiftCertificate expected = new GiftCertificate();
        expected.setId(1L);
        expected.setName("test");

        GiftCertificate actual = dao.update(expected);

        assertThat(expected.getName()).isEqualTo(actual.getName());

    }

    @Test
    void checkDeleteCertificateTagByCertificateIdShouldSuccess() {
        dao = Mockito.mock(GiftCertificateDao.class);
        dao.deleteCertificateTagByCertificateId(1L);
        Mockito.verify(dao).deleteCertificateTagByCertificateId(1L);
    }

    @Test
    void checkCreateShouldEquals() {
        GiftCertificate expected = new GiftCertificate();
        expected.setName("test name");
        expected.setDescription("test description");
        expected.setPrice(BigDecimal.valueOf(1.11));
        expected.setDuration(1);

        GiftCertificate actual = dao.create(expected);
        actual.setCreatedDate(null);
        actual.setLastUpdateDate(null);
        expected.setId(actual.getId());

        assertThat(actual).isEqualTo(expected);
    }
}