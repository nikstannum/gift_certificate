package ru.clevertec.ecl.integration.service;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import ru.clevertec.ecl.integration.BaseIntegrationTest;
import ru.clevertec.ecl.service.dto.GiftCertificateDto;
import ru.clevertec.ecl.service.dto.QueryParamsDto;
import ru.clevertec.ecl.service.dto.TagDto;
import ru.clevertec.ecl.service.exception.ClientException;
import ru.clevertec.ecl.service.exception.NotFoundException;
import ru.clevertec.ecl.service.impl.GiftCertificateServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

public class GiftCertificateServiceTest extends BaseIntegrationTest {

    @Autowired
    private GiftCertificateServiceImpl service;
    @Autowired
    private EntityManager manager;

    @Test
    void checkCreateByParamsShouldReturnNotNullId() {
        QueryParamsDto queryParamsDto = new QueryParamsDto();
        queryParamsDto.setCert("name:created,descr:created,price:123.45,duration:1");
        queryParamsDto.setTag("name:created");

        GiftCertificateDto actual = service.createByParams(queryParamsDto);

        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void checkCreateByParamsShouldThrowClientExc() {
        QueryParamsDto queryParamsDto = new QueryParamsDto();
        queryParamsDto.setCert("name:created,descr:back massage lasting 1 hour,price:123.45,duration:1");
        queryParamsDto.setTag("name:created");

        Assertions.assertThrows(ClientException.class, () -> service.createByParams(queryParamsDto));
    }

    @Test
    void checkUpdateShouldReturnTagsSize3() {
        GiftCertificateDto expected = new GiftCertificateDto();
        expected.setId(1L);
        expected.setName("updated");
        expected.setDescription("updated");
        expected.setPrice(BigDecimal.valueOf(11.11));
        expected.setDuration(15);
        TagDto tagDto = new TagDto();
        tagDto.setName("updated");
        TagDto tagMale = new TagDto();
        tagMale.setId(1L);
        tagMale.setName("male");
        TagDto tagExtreme = new TagDto();
        tagExtreme.setId(3L);
        tagExtreme.setName("extreme");
        expected.setTags(List.of(tagDto, tagMale, tagExtreme));

        GiftCertificateDto actual = service.update(expected);

        assertThat(actual.getTags()).hasSize(3);
    }

    @Test
    void checkUpdateShouldReturnDescrEquals() {
        GiftCertificateDto expected = new GiftCertificateDto();
        expected.setId(1L);
        expected.setName("updated");
        expected.setDescription("updated");
        expected.setPrice(BigDecimal.valueOf(11.11));
        expected.setDuration(15);
        TagDto tagDto = new TagDto();
        tagDto.setName("updated");
        TagDto tagMale = new TagDto();
        tagMale.setId(1L);
        tagMale.setName("male");
        TagDto tagExtreme = new TagDto();
        tagExtreme.setId(3L);
        tagExtreme.setName("extreme");
        expected.setTags(List.of(tagDto, tagMale, tagExtreme));

        GiftCertificateDto actual = service.update(expected);

        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
    }

    @Test
    void checkCreateShouldReturnNotNullId() {
        GiftCertificateDto dto = new GiftCertificateDto();
        dto.setName("created");
        dto.setDescription("created");
        dto.setPrice(BigDecimal.valueOf(123.12));
        dto.setDuration(12);

        GiftCertificateDto actual = service.create(dto);

        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void checkUpdateByParamsShouldReturnDescEquals() {
        QueryParamsDto queryParamsDto = new QueryParamsDto();
        queryParamsDto.setCert("name:updated,descr:updated");

        GiftCertificateDto actual = service.updateByParams(queryParamsDto, 1L);

        assertThat(actual.getDescription()).isEqualTo("updated");
    }

    @Test
    void checkUpdateByParamsShouldThrowClientExc() {
        QueryParamsDto queryParamsDto = new QueryParamsDto();
        queryParamsDto.setCert("name:updated,descr:monthly membership to the CROSSFIT gym");

        Assertions.assertThrows(ClientException.class, () -> service.updateByParams(queryParamsDto, 1L));
    }

    @Test
    void checkFindAllShouldReturnSize3() {
        int expectedSize = 3;
        Pageable pageable = PageRequest.of(0, 3, Direction.ASC, "id");
        Page<GiftCertificateDto> actual = service.findAll(pageable);
        assertThat(actual).hasSize(expectedSize);
    }

    @Test
    void checkFindByParamsShouldReturnNotEmpty() {
        QueryParamsDto queryParamsDto = new QueryParamsDto();
        queryParamsDto.setCert("descr:like:air");

        List<GiftCertificateDto> actual = service.findByParams(queryParamsDto);

        assertThat(actual).isNotEmpty();
    }

    @Test
    void checkFindByParamsShouldReturnEmpty() {
        QueryParamsDto queryParamsDto = new QueryParamsDto();
        queryParamsDto.setCert("descr:eq:air");

        List<GiftCertificateDto> actual = service.findByParams(queryParamsDto);

        assertThat(actual).isEmpty();
    }

    @Test
    void checkFindByIdShouldReturnNotNull() {
        assertThat(service.findById(1L)).isNotNull();
    }

    @Test
    void checkFindByIdShouldThrowNotFoundExc() {
        Assertions.assertThrows(NotFoundException.class, () -> service.findById(100L));
    }

    @Test
    void checkDeleteShouldSuccess() {
        service.delete(5L);
        manager.flush();
    }
}
