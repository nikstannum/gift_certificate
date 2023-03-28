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
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.data.repository.GiftCertificateRepository;
import ru.clevertec.ecl.service.dto.GiftCertificateDto;
import ru.clevertec.ecl.service.dto.QueryParamsDto;
import ru.clevertec.ecl.service.dto.TagDto;
import ru.clevertec.ecl.service.exception.NotFoundException;
import ru.clevertec.ecl.service.mapper.GiftCertificateMapperImpl;
import ru.clevertec.ecl.service.mapper.Mapper;
import ru.clevertec.ecl.service.mapper.QueryMapperImpl;
import ru.clevertec.ecl.service.mapper.TagMapperImpl;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GiftCertificateServiceImplTest {

    public static final String PAGE_SIZE_2 = "2";
    private static final String TAG_NAME_MALE = "male";
    private static final String TAG_NAME_EXTREME = "extreme";
    private static final String TAG_NAME_TEST = "test";
    public static final String CERT_NAME_SKYDIVING = "skydiving";
    public static final String STD_CERT_PARAMS_CREATE_UPD = "name:test name,descr:test description,price:1.11,duration:1";
    public static final String STD_TAG_PARAMS_CREATE_UPD = "name:male,name:test";
    public static final String TEST_NAME = "test name";
    public static final String TEST_DESCRIPTION = "test description";

    private final TagMapperImpl tag = new TagMapperImpl();
    private final QueryMapperImpl queryMapper = new QueryMapperImpl();
    private final GiftCertificateMapperImpl certificateMapper = new GiftCertificateMapperImpl();
    private Mapper mapper = new Mapper(tag, certificateMapper, queryMapper);
    private GiftCertificateServiceImpl service;

    @Mock
    private GiftCertificateRepository repository;

    @BeforeEach
    void setUp() {
        service = new GiftCertificateServiceImpl(repository, mapper);
    }

    @Test
    void checkFindByParamsShouldReturn5() {
        List<GiftCertificate> list = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            GiftCertificate certificate = getEmptyCertificateById(i);
            list.add(certificate);
        }
        Mockito.when(repository.find(new QueryParams())).thenReturn(list);
        List<GiftCertificateDto> listDto = service.findByParams(new QueryParamsDto());

        assertThat(listDto).hasSize(5);
    }

    private GiftCertificate getEmptyCertificateById(Long id) {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(id);
        return certificate;
    }

    @Test
    void checkFindByParamsShouldReturn2() {
        List<GiftCertificate> list = new ArrayList<>();
        for (long i = 1; i <= 2; i++) {
            GiftCertificate certificate = getEmptyCertificateById(i);
            list.add(certificate);
        }
        QueryParams params = new QueryParams();
        params.setSize(PAGE_SIZE_2);
        QueryParamsDto paramsDto = new QueryParamsDto();
        paramsDto.setSize(PAGE_SIZE_2);
        Mockito.when(repository.find(params)).thenReturn(list);

        List<GiftCertificateDto> listDto = service.findByParams(paramsDto);

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
        certificate.setName(CERT_NAME_SKYDIVING);
        Mockito.when(repository.findById(1L)).thenReturn(certificate);

        GiftCertificateDto actual = service.findById(1L);

        assertThat(actual.getName()).isEqualTo(CERT_NAME_SKYDIVING);
    }

    @Test
    void checkCreateShouldEquals() {
        prepareMapperMockCreate();
        prepareRepoMockCreate();
        service = new GiftCertificateServiceImpl(repository, mapper);
        GiftCertificateDto expected = getGiftCertificateDtoWithoutTags();
        TagDto tag1 = createTagDto(1L, TAG_NAME_MALE);
        TagDto tag3 = createTagDto(4L, TAG_NAME_TEST);
        List<TagDto> tags = List.of(tag1, tag3);
        expected.setTags(tags);

        GiftCertificateDto actual = service.create(getQueryParamsDto());

        assertThat(actual).isEqualTo(expected);
    }

    private void prepareMapperMockCreate() {
        mapper = Mockito.mock(Mapper.class);
        QueryParamsDto paramsDto = getQueryParamsDto();
        QueryParams params = getQueryParams();
        Mockito.doReturn(params).when(mapper).convert(paramsDto);

        Tag tag1 = createTag(1L, TAG_NAME_MALE);
        TagDto tagDto1 = createTagDto(1L, TAG_NAME_MALE);
        Tag tag3 = createTag(4L, TAG_NAME_TEST);
        TagDto tagDto3 = createTagDto(4L, TAG_NAME_TEST);
        GiftCertificate certCreate = getGiftCertificateWithoutTag();
        GiftCertificateDto dtoCreate = getGiftCertificateDtoWithoutTags();
        certCreate.setTags(List.of(tag1, tag3));
        dtoCreate.setTags(List.of(tagDto1, tagDto3));
        Mockito.doReturn(dtoCreate).when(mapper).convert(certCreate);
    }

    private void prepareRepoMockCreate() {
        QueryParams params = getQueryParams();
        GiftCertificate certificate = getGiftCertificateWithoutTag();
        Mockito.doReturn(certificate).when(repository).createByParams(params);
        Mockito.doReturn(Optional.empty()).when(repository).findTagByName(TAG_NAME_TEST);
        Tag tag = createTag(1L, TAG_NAME_MALE);
        Mockito.doReturn(Optional.of(tag)).when(repository).findTagByName(TAG_NAME_MALE);
        Tag tagForCreation = new Tag();
        tagForCreation.setName(TAG_NAME_TEST);
        Tag createdTag = createTag(4L, TAG_NAME_TEST);
        Mockito.doReturn(createdTag).when(repository).createTag(tagForCreation);
    }

    private GiftCertificateDto getGiftCertificateDtoWithoutTags() {
        GiftCertificateDto certDto = new GiftCertificateDto();
        certDto.setId(1L);
        certDto.setName(TEST_NAME);
        certDto.setDescription(TEST_DESCRIPTION);
        certDto.setPrice(BigDecimal.valueOf(1.11));
        certDto.setDuration(1);
        return certDto;
    }

    private TagDto createTagDto(Long id, String name) {
        TagDto tagDto = new TagDto();
        tagDto.setId(id);
        tagDto.setName(name);
        return tagDto;
    }

    private QueryParamsDto getQueryParamsDto() {
        QueryParamsDto paramsDto = new QueryParamsDto();
        paramsDto.setCert(STD_CERT_PARAMS_CREATE_UPD);
        paramsDto.setTag(STD_TAG_PARAMS_CREATE_UPD);
        return paramsDto;
    }

    private QueryParams getQueryParams() {
        QueryParams params = new QueryParams();
        params.setCert(STD_CERT_PARAMS_CREATE_UPD);
        params.setTag(STD_TAG_PARAMS_CREATE_UPD);
        return params;
    }

    private Tag createTag(Long id, String name) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        return tag;
    }

    private GiftCertificate getGiftCertificateWithoutTag() {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(1L);
        certificate.setName(TEST_NAME);
        certificate.setDescription(TEST_DESCRIPTION);
        certificate.setPrice(BigDecimal.valueOf(1.11));
        certificate.setDuration(1);
        return certificate;
    }

    @Test
    void checkUpdateShouldReturnEquals() {
        prepareMapperMockUpdate();
        prepareRepoMockUpdate();
        service = new GiftCertificateServiceImpl(repository, mapper);
        GiftCertificateDto expected = getGiftCertificateDtoWithoutTags();
        TagDto tagDto1 = createTagDto(1L, TAG_NAME_MALE);
        TagDto tagDto2 = createTagDto(3L, TAG_NAME_EXTREME);
        TagDto tagDto3 = createTagDto(4L, TAG_NAME_TEST);
        List<TagDto> tagDtoList = List.of(tagDto1, tagDto2, tagDto3);
        expected.setTags(tagDtoList);

        GiftCertificateDto actual = service.update(getQueryParamsDto(), 1L);

        assertThat(actual).isEqualTo(expected);
    }

    private void prepareMapperMockUpdate() {
        mapper = Mockito.mock(Mapper.class);
        QueryParamsDto paramsDto = getQueryParamsDto();
        QueryParams params = getQueryParams();
        Mockito.doReturn(params).when(mapper).convert(paramsDto);

        Tag tag1 = createTag(1L, TAG_NAME_MALE);
        TagDto tagDto1 = createTagDto(1L, TAG_NAME_MALE);
        Tag tag3 = createTag(4L, TAG_NAME_TEST);
        TagDto tagDto3 = createTagDto(4L, TAG_NAME_TEST);
        GiftCertificate certUpd = getGiftCertificateWithoutTag();
        GiftCertificateDto dtoUpd = getGiftCertificateDtoWithoutTags();
        Tag tag2 = createTag(3L, TAG_NAME_EXTREME);
        TagDto tagDto2 = createTagDto(3L, TAG_NAME_EXTREME);
        certUpd.setTags(List.of(tag1, tag2, tag3));
        dtoUpd.setTags(List.of(tagDto1, tagDto2, tagDto3));
        Mockito.doReturn(dtoUpd).when(mapper).convert(certUpd);
    }

    private void prepareRepoMockUpdate() {
        Tag tag1 = createTag(1L, TAG_NAME_MALE);
        Tag tag2 = createTag(3L, TAG_NAME_EXTREME);
        List<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        tags.add(tag2);
        Mockito.doReturn(tags).when(repository).findTagsByCertificateId(1L);
        Tag tagForCreation = new Tag();
        tagForCreation.setName(TAG_NAME_TEST);
        Tag createdTag = createTag(4L, TAG_NAME_TEST);
        Mockito.doReturn(createdTag).when(repository).createTag(tagForCreation);
        QueryParams params = getQueryParams();
        GiftCertificate cert = getGiftCertificateWithoutTag();
        Mockito.doReturn(cert).when(repository).updateByParams(params, 1L);
    }

    @Captor
    ArgumentCaptor<Long> captor;

    @Test
    void checkDeleteShouldCapture1() {
        service.delete(1L);
        Mockito.verify(repository).delete(captor.capture());
        Long actual = captor.getValue();

        assertThat(actual).isEqualTo(1L);
    }
}