package ru.clevertec.ecl.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.data.repository.GiftCertificateRepository;
import ru.clevertec.ecl.data.repository.TagRepository;
import ru.clevertec.ecl.service.dto.GiftCertificateDto;
import ru.clevertec.ecl.service.dto.QueryParamsDto;
import ru.clevertec.ecl.service.dto.TagDto;
import ru.clevertec.ecl.service.exception.ClientException;
import ru.clevertec.ecl.service.exception.NotFoundException;
import ru.clevertec.ecl.service.mapper.Mapper;
import ru.clevertec.ecl.service.util.builder.CertificateBuilder;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GiftCertificateServiceImplTest {

    public static final String PARAM_CERT_CREATE = "name:name,descr:description,price:1.11,duration:1";
    public static final String PARAM_TAG_CREATE = "name:tag1,name:tag2";
    public static final String DESCR_EXISTS = "descr:exists";
    public static final String DESCR_EQ_EXISTS = "descr:eq:exists";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final BigDecimal PRICE = BigDecimal.valueOf(1.11);
    public static final int DURATION = 1;
    public static final String TAG_1 = "tag1";
    public static final String TAG_2 = "tag2";
    public static final String EXISTS = "exists";

    @Mock
    private GiftCertificateRepository certificateRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private Mapper mapper;
    @Mock
    private CertificateBuilder builder;

    @InjectMocks
    private GiftCertificateServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new GiftCertificateServiceImpl(certificateRepository, tagRepository, builder, mapper);
    }

    @Test
    void checkCreateShouldReturnEquals() {
        QueryParams queryParams = getQueryParams();
        QueryParamsDto paramsDtoCreate = getQueryParamsDto();
        Mockito.doReturn(queryParams).when(mapper).convert(paramsDtoCreate);
        GiftCertificate certificate = getCertificate();
        GiftCertificateDto certificateDto = getCertificateDto();
        Mockito.doReturn(certificateDto).when(mapper).convert(certificate);
        Mockito.doReturn(certificate).when(builder).buildCertificate(queryParams);
        Mockito.doReturn(certificate).when(certificateRepository).create(certificate);

        GiftCertificateDto actual = service.create(paramsDtoCreate);

        assertThat(actual).isEqualTo(certificateDto);
    }

    private QueryParamsDto getQueryParamsDto() {
        QueryParamsDto paramsDtoCreate = new QueryParamsDto();
        paramsDtoCreate.setCert(PARAM_CERT_CREATE);
        paramsDtoCreate.setTag(PARAM_TAG_CREATE);
        return paramsDtoCreate;
    }

    private QueryParams getQueryParams() {
        QueryParams queryParams = new QueryParams();
        queryParams.setCert(PARAM_CERT_CREATE);
        queryParams.setTag(PARAM_TAG_CREATE);
        return queryParams;
    }

    private GiftCertificateDto getCertificateDto() {
        GiftCertificateDto certificate = new GiftCertificateDto();
        certificate.setName(NAME);
        certificate.setDescription(DESCRIPTION);
        certificate.setDuration(DURATION);
        certificate.setPrice(PRICE);
        TagDto tag1 = createTagDto(null, TAG_1);
        TagDto tag2 = createTagDto(null, TAG_2);
        List<TagDto> tags = new ArrayList<>();
        tags.add(tag1);
        tags.add(tag2);
        certificate.setTags(tags);
        return certificate;
    }

    private GiftCertificate getCertificate() {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setName(NAME);
        certificate.setDescription(DESCRIPTION);
        certificate.setDuration(DURATION);
        certificate.setPrice(PRICE);
        Tag tag1 = createTag(null, TAG_1);
        Tag tag2 = createTag(null, TAG_2);
        List<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        tags.add(tag2);
        certificate.setTags(tags);
        return certificate;
    }

    @Test
    void checkCreateShouldThrowClientExc() {
        prepareMocksShouldThrowClientExc();
        QueryParamsDto paramsDto = new QueryParamsDto();
        paramsDto.setCert(DESCR_EXISTS);
        Assertions.assertThrows(ClientException.class, () -> service.create(paramsDto));
    }

    private void prepareMocksShouldThrowClientExc() {
        QueryParamsDto paramsDtoDescrExists = new QueryParamsDto();
        paramsDtoDescrExists.setCert(DESCR_EXISTS);
        QueryParams paramsDescrExists = new QueryParams();
        paramsDescrExists.setCert(DESCR_EXISTS);
        Mockito.doReturn(paramsDescrExists).when(mapper).convert(paramsDtoDescrExists);

        QueryParams paramsDescrEqExists = new QueryParams();
        paramsDescrEqExists.setCert(DESCR_EQ_EXISTS);
        List<GiftCertificate> list = List.of(new GiftCertificate());
        Mockito.doReturn(list).when(certificateRepository).findByParams(paramsDescrEqExists);
    }

    @Test
    void checkUpdateShouldThrowClientExc() {
        prepareMocksShouldThrowClientExc();
        QueryParamsDto paramsDto = new QueryParamsDto();
        paramsDto.setCert(DESCR_EXISTS);
        Assertions.assertThrows(ClientException.class, () -> service.update(paramsDto, 1L));
    }

    @Test
    void checkUpdateShouldReturnEquals() {
        QueryParams queryParams = getQueryParams();
        QueryParamsDto paramsDtoCreate = getQueryParamsDto();
        Mockito.doReturn(queryParams).when(mapper).convert(paramsDtoCreate);
        GiftCertificate certificate = getCertificate();
        GiftCertificateDto certificateDto = getCertificateDto();
        Mockito.doReturn(certificateDto).when(mapper).convert(certificate);
        Mockito.doReturn(certificate).when(builder).buildCertificate(queryParams);
        Mockito.doReturn(certificate).when(certificateRepository).findById(1L);
        Mockito.doReturn(certificate).when(certificateRepository).update(certificate);

        GiftCertificateDto actual = service.update(paramsDtoCreate, 1L);

        assertThat(actual).isEqualTo(certificateDto);
    }

    @Test
    void checkFindAllShouldSize2() {
        GiftCertificate certificate = getCertificate();
        GiftCertificateDto certificateDto = getCertificateDto();
        QueryParams queryParams = new QueryParams();
        List<GiftCertificate> list = List.of(certificate, certificate);
        Mockito.doReturn(list).when(certificateRepository).findByParams(queryParams);
        QueryParamsDto paramsDto = new QueryParamsDto();
        Mockito.doReturn(queryParams).when(mapper).convert(paramsDto);
        Mockito.doReturn(certificateDto).when(mapper).convert(certificate);

        List<GiftCertificateDto> actual = service.findAll(paramsDto);

        assertThat(actual).hasSize(2);
    }

    @Test
    void checkFindByParamsShouldSize1() {
        QueryParamsDto paramsDto = new QueryParamsDto();
        paramsDto.setCert(DESCR_EQ_EXISTS);
        QueryParams queryParams = new QueryParams();
        queryParams.setCert(DESCR_EQ_EXISTS);
        Mockito.doReturn(queryParams).when(mapper).convert(paramsDto);
        GiftCertificate certificate = new GiftCertificate();
        certificate.setDescription(EXISTS);
        GiftCertificateDto certificateDto = new GiftCertificateDto();
        certificateDto.setDescription(EXISTS);
        Mockito.doReturn(certificateDto).when(mapper).convert(certificate);
        List<GiftCertificate> list = new ArrayList<>();
        list.add(certificate);
        Mockito.doReturn(list).when(certificateRepository).findByParams(queryParams);

        List<GiftCertificateDto> dtos = service.findByParams(paramsDto);

        assertThat(dtos).hasSize(1);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 200})
    void checkFindByIdShouldThrowNotFoundExc(Long id) {
        Mockito.doReturn(null).when(certificateRepository).findById(id);
        Assertions.assertThrows(NotFoundException.class, () -> service.findById(id));
    }

    @Test
    void checkFindByIdShouldReturnEquals() {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(1L);
        Mockito.doReturn(certificate).when(certificateRepository).findById(1L);
        GiftCertificateDto dto = new GiftCertificateDto();
        dto.setId(1L);
        Mockito.doReturn(dto).when(mapper).convert(certificate);

        GiftCertificateDto actual = service.findById(1L);

        assertThat(actual.getId()).isEqualTo(1L);
    }

    @Captor
    ArgumentCaptor<Long> captor;

    @Test
    void delete() {
        service.delete(1L);
        Mockito.verify(certificateRepository).delete(captor.capture());
        Long actual = captor.getValue();

        assertThat(actual).isEqualTo(1L);
    }

    private TagDto createTagDto(Long id, String name) {
        TagDto tagDto = new TagDto();
        tagDto.setId(id);
        tagDto.setName(name);
        return tagDto;
    }

    private Tag createTag(Long id, String name) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        return tag;
    }
}