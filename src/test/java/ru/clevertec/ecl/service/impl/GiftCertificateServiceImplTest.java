package ru.clevertec.ecl.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.clevertec.ecl.ContextConfig;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.data.repository.GiftCertificateRepository;
import ru.clevertec.ecl.service.GiftCertificateService;
import ru.clevertec.ecl.service.dto.GiftCertificateDto;
import ru.clevertec.ecl.service.dto.QueryParamsDto;
import ru.clevertec.ecl.service.dto.TagDto;
import ru.clevertec.ecl.service.exception.NotFoundException;
import ru.clevertec.ecl.service.mapper.Mapper;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(ContextConfig.class)
@ExtendWith(MockitoExtension.class)
class GiftCertificateServiceImplTest {

    private GiftCertificateService service;

    @Mock
    private GiftCertificateRepository repository;

    private final Mapper mapper;

    @Autowired
    GiftCertificateServiceImplTest(Mapper mapper) {
        this.mapper = mapper;
    }

    @BeforeEach
    void setUp() {
        service = new GiftCertificateServiceImpl(repository, mapper);
    }

    private GiftCertificate getEmptyCertificateById(Long id) {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(id);
        return certificate;
    }

    @Test
    void checkFindShouldReturn5() {
        List<GiftCertificate> list = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            GiftCertificate certificate = getEmptyCertificateById(i);
            list.add(certificate);
        }
        Mockito.when(repository.find(new QueryParams())).thenReturn(list);
        List<GiftCertificateDto> listDto = service.find(new QueryParamsDto());

        assertThat(listDto).hasSize(5);
    }

    @Test
    void checkFindShouldReturn2() {
        List<GiftCertificate> list = new ArrayList<>();
        for (long i = 1; i <= 2; i++) {
            GiftCertificate certificate = getEmptyCertificateById(i);
            list.add(certificate);
        }
        QueryParams params = new QueryParams();
        params.setSize("2");
        QueryParamsDto paramsDto = new QueryParamsDto();
        paramsDto.setSize("2");
        Mockito.when(repository.find(params)).thenReturn(list);

        List<GiftCertificateDto> listDto = service.find(paramsDto);

        assertThat(listDto).hasSize(2);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 200})
    void checkFindByIdShouldThrowNotFoundExc(Long id) {
        Mockito.when(repository.findById(id)).thenThrow(NotFoundException.class);
        org.junit.jupiter.api.Assertions.assertThrows(NotFoundException.class, () -> service.findById(id));
    }

    @Test
    void checkFindByIdShouldReturnEquals() {
        GiftCertificate certificate = getEmptyCertificateById(1L);
        certificate.setName("skydiving");
        Mockito.when(repository.findById(1L)).thenReturn(certificate);

        GiftCertificateDto actual = service.findById(1L);

        assertThat(actual.getName()).isEqualTo("skydiving");
    }

    @Test
    void create() {
        QueryParamsDto paramsDto = getQueryParamsDto();
        prepareRepoMockCreate();
        GiftCertificateDto expected = getGiftCertificateDtoWithoutTags();
        TagDto tag1 = createTagDto(1L, "male");
        TagDto tag3 = createTagDto(4L, "test");
        List<TagDto> tags = List.of(tag1, tag3);
        expected.setTags(tags);

        GiftCertificateDto actual = service.create(paramsDto);

        assertThat(actual).isEqualTo(expected);
    }

    private void prepareRepoMockCreate() {
        QueryParams params = mapper.convert(getQueryParamsDto());
        GiftCertificate certificate = getGiftCertificate();
        Mockito.when(repository.createByParams(params)).thenReturn(certificate);
        Mockito.when(repository.findTagByName("test")).thenReturn(Optional.empty());
        Tag tag = createTag(1L, "male");
        Mockito.when(repository.findTagByName("male")).thenReturn(Optional.of(tag));
        Tag tagForCreation = new Tag();
        tagForCreation.setName("test");
        Tag createdTag = createTag(4L, "test");
        Mockito.when(repository.createTag(tagForCreation)).thenReturn(createdTag);
    }

    private GiftCertificate getGiftCertificate() {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(1L);
        certificate.setName("test name");
        certificate.setDescription("test description");
        certificate.setPrice(BigDecimal.valueOf(1.11));
        certificate.setDuration(1);
        return certificate;
    }

    private GiftCertificateDto getGiftCertificateDtoWithoutTags() {
        GiftCertificateDto certDto = new GiftCertificateDto();
        certDto.setId(1L);
        certDto.setName("test name");
        certDto.setDescription("test description");
        certDto.setPrice(BigDecimal.valueOf(1.11));
        certDto.setDuration(1);
        return certDto;
    }

    private Tag createTag(Long id, String name) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        return tag;
    }

    private TagDto createTagDto(Long id, String name) {
        TagDto tagDto = new TagDto();
        tagDto.setId(id);
        tagDto.setName(name);
        return tagDto;
    }

    private QueryParamsDto getQueryParamsDto() {
        QueryParamsDto paramsDto = new QueryParamsDto();
        paramsDto.setCert("name:test name,descr:test description,price:1.11,duration:1");
        paramsDto.setTag("name:male,name:test");
        return paramsDto;
    }

    @Test
    void update() {
        prepareRepoMockUpdate();
        GiftCertificateDto expected = getGiftCertificateDtoWithoutTags();
        TagDto tagDto1 = createTagDto(1L, "male");
        TagDto tagDto2 = createTagDto(3L, "extreme");
        TagDto tagDto3 = createTagDto(4L, "test");
        List<TagDto> tagDtoList = List.of(tagDto1, tagDto2, tagDto3);
        expected.setTags(tagDtoList);

        GiftCertificateDto actual = service.update(getQueryParamsDto(), 1L);

        assertThat(actual).isEqualTo(expected);
    }

    private void prepareRepoMockUpdate() {
        Tag tag1 = createTag(1L, "male");
        Tag tag2 = createTag(3L, "extreme");
        List<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        tags.add(tag2);
        Mockito.when(repository.findTagsByCertificateId(1L)).thenReturn(tags);
        Tag tagForCreation = new Tag();
        tagForCreation.setName("test");
        Tag createdTag = createTag(4L, "test");
        Mockito.when(repository.createTag(tagForCreation)).thenReturn(createdTag);
        QueryParams params = mapper.convert(getQueryParamsDto());
        GiftCertificate cert = getGiftCertificate();
        Mockito.when(repository.updateByParams(params, 1L)).thenReturn(cert);
    }

    @Captor
    ArgumentCaptor<Long> captor;

    @Test
    void delete() {
        service.delete(1L);
        Mockito.verify(repository).delete(captor.capture());
        Long actual = captor.getValue();

        assertThat(actual).isEqualTo(1L);
    }
}