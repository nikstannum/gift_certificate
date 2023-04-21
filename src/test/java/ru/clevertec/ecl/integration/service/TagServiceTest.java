package ru.clevertec.ecl.integration.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import ru.clevertec.ecl.integration.BaseIntegrationTest;
import ru.clevertec.ecl.service.dto.TagDto;
import ru.clevertec.ecl.service.exception.NotFoundException;
import ru.clevertec.ecl.service.impl.TagServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;


class TagServiceTest extends BaseIntegrationTest {

    @Autowired
    private TagServiceImpl service;
    @Autowired
    private EntityManager manager;

    @Test
    void checkFindPopularTagShouldReturnNotNull() {
        TagDto actual = service.findPopularTag(1L);
        assertThat(actual).isNotNull();
    }

    @Test
    void checkFindByIdShouldReturnNotNull() {
        TagDto actual = service.findById(1L);
        assertThat(actual).isNotNull();
    }

    @Test
    void checkFindByIdShouldThrowNotFoundExc() {
        Assertions.assertThrows(NotFoundException.class, () -> service.findById(100L));
    }

    @Test
    void checkUpdateShouldReturnEquals() {
        TagDto expected = new TagDto();
        expected.setId(1L);
        expected.setName("updated");

        TagDto actual = service.update(expected);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkFindAllShouldReturn3() {
        int expectedSize = 3;
        Pageable pageable = PageRequest.of(0, 3, Direction.ASC, "id");
        Page<TagDto> actual = service.findAll(pageable);
        assertThat(actual).hasSize(expectedSize);
    }

    @Test
    void checkSaveTagShouldNotNullId() {
        TagDto tagDto = new TagDto();
        tagDto.setName("test");
        TagDto actual = service.create(tagDto);
        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void checkDelete() {
        service.delete(1L);
        manager.flush();
    }
}
