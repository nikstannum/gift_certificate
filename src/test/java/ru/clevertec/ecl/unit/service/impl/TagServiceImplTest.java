package ru.clevertec.ecl.unit.service.impl;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.data.repository.TagRepository;
import ru.clevertec.ecl.service.dto.TagDto;
import ru.clevertec.ecl.service.exception.NotFoundException;
import ru.clevertec.ecl.service.impl.TagServiceImpl;
import ru.clevertec.ecl.service.mapper.Mapper;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;
    @Mock
    private Mapper mapper;
    @InjectMocks
    private TagServiceImpl service;


    @Test
    void checkFindPopularTagShouldReturnEquals() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        Mockito.doReturn(tag).when(tagRepository).findMostPopularTag(1L);
        TagDto expected = new TagDto();
        expected.setId(1L);
        expected.setName("test");
        Mockito.doReturn(expected).when(mapper).convert(tag);

        TagDto actual = service.findPopularTag(1L);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkFindByIdShouldReturnEquals() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        Mockito.doReturn(Optional.of(tag)).when(tagRepository).findById(1L);
        TagDto expected = new TagDto();
        expected.setId(1L);
        expected.setName("test");
        Mockito.doReturn(expected).when(mapper).convert(tag);

        TagDto actual = service.findById(1L);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 200})
    void checkFindByIdShouldThrowNotFoundExc(Long id) {
        Mockito.doReturn(Optional.empty()).when(tagRepository).findById(id);

        Assertions.assertThrows(NotFoundException.class, () -> service.findById(id));
    }

    @Test
    void checkUpdateShouldReturnEquals() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        TagDto dto = new TagDto();
        dto.setId(1L);
        dto.setName("test");
        Mockito.doReturn(tag).when(tagRepository).save(tag);
        Mockito.doReturn(dto).when(mapper).convert(tag);
        Mockito.doReturn(tag).when(mapper).convert(dto);

        TagDto actual = service.update(dto);

        assertThat(actual).isEqualTo(dto);
    }

    @Test
    void checkFindAllShouldHasSize2() {
        List<Tag> list = List.of(new Tag(), new Tag());
        Pageable pageable = PageRequest.of(0, 2, Direction.ASC, "id");
        Page<Tag> tagPage = new PageImpl<>(list);
        Mockito.doReturn(tagPage).when(tagRepository).findAll(pageable);

        assertThat(service.findAll(pageable)).hasSize(2);
    }

    @Test
    void checkCreateShouldHasIdNotNull() {
        Tag tag = new Tag();
        tag.setName("test");
        Tag created = new Tag();
        TagDto dtoForCreation = new TagDto();
        dtoForCreation.setName("test");
        Mockito.doReturn(tag).when(mapper).convert(dtoForCreation);
        created.setId(1L);
        created.setName("test");
        Mockito.doReturn(created).when(tagRepository).save(tag);
        TagDto expected = new TagDto();
        expected.setId(1L);
        expected.setName("test");
        Mockito.doReturn(expected).when(mapper).convert(created);

        TagDto actual = service.create(dtoForCreation);

        assertThat(actual.getId()).isNotNull();
    }


    @Captor
    ArgumentCaptor<Long> captor;

    @Test
    void checkDeleteShouldCapture() {
        service.delete(1L);
        Mockito.verify(tagRepository).deleteById(captor.capture());

        Long actual = captor.getValue();

        assertThat(actual).isEqualTo(1L);
    }
}