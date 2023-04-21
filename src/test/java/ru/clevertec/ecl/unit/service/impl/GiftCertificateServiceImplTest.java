package ru.clevertec.ecl.unit.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.springframework.data.jpa.domain.Specification;
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
import ru.clevertec.ecl.service.impl.GiftCertificateServiceImpl;
import ru.clevertec.ecl.service.mapper.Mapper;
import ru.clevertec.ecl.service.util.CertificateBuilder;
import ru.clevertec.ecl.service.util.PagingUtil;
import ru.clevertec.ecl.service.util.SpecificationBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GiftCertificateServiceImplTest {

    private static final String PARAM_CERT_CREATE = "name:name,descr:description,price:1.11,duration:1";
    private static final String PARAM_TAG_CREATE = "name:tag1,name:tag2";
    private static final String DESCR_EXISTS = "descr:exists";
    private static final String DESCR_EQ_EXISTS = "descr:eq:exists";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final BigDecimal PRICE = BigDecimal.valueOf(1.11);
    private static final int DURATION = 1;
    private static final String TAG_1 = "tag1";
    private static final String TAG_2 = "tag2";

    @Mock
    private GiftCertificateRepository certificateRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private CertificateBuilder certificateBuilder;
    @Mock
    private final SpecificationBuilder specificationBuilder = new SpecificationBuilder();
    @Mock
    private PagingUtil pagingUtil;
    @Mock
    private Mapper mapper;
    @InjectMocks
    private GiftCertificateServiceImpl service;

    @Test
    void checkCreateByParamsShouldReturnEquals() {
        QueryParams queryParams = getQueryParams();
        QueryParamsDto paramsDtoCreate = getQueryParamsDto();
        Mockito.doReturn(queryParams).when(mapper).convert(paramsDtoCreate);
        GiftCertificate certificate = getCertificate();
        GiftCertificateDto certificateDto = getCertificateDto();
        Mockito.doReturn(certificateDto).when(mapper).convert(certificate);
        Mockito.doReturn(certificate).when(certificateBuilder).buildCertificate(queryParams);
        Mockito.doReturn(certificate).when(certificateRepository).save(certificate);

        GiftCertificateDto actual = service.createByParams(paramsDtoCreate);

        assertThat(actual).isEqualTo(certificateDto);
    }

    @Test
    void checkCreateByParamsShouldThrowClientExc() {
        prepareCertRepoMocksReturningExistingDbValue();
        QueryParamsDto paramsDto = new QueryParamsDto();
        paramsDto.setCert(DESCR_EXISTS);
        Assertions.assertThrows(ClientException.class, () -> service.createByParams(paramsDto));
    }

    private void prepareCertRepoMocksReturningExistingDbValue() {
        QueryParamsDto paramsDtoDescrExists = new QueryParamsDto();
        paramsDtoDescrExists.setCert(DESCR_EXISTS);
        QueryParams paramsDescrExists = new QueryParams();
        paramsDescrExists.setCert(DESCR_EXISTS);
        Mockito.doReturn(paramsDescrExists).when(mapper).convert(paramsDtoDescrExists);

        QueryParams paramsDescrEqExists = new QueryParams();
        paramsDescrEqExists.setCert(DESCR_EQ_EXISTS);
        List<GiftCertificate> list = List.of(new GiftCertificate());
        Specification<GiftCertificate> specification = specificationBuilder.getSpecificationsSelectCertificateByParams(paramsDescrEqExists);

        Mockito.doReturn(list).when(certificateRepository).findAll(specification);
    }

    @Test
    void checkUpdateByParamsShouldThrowClientExc() {
        prepareCertRepoMocksReturningExistingDbValue();
        QueryParamsDto paramsDto = new QueryParamsDto();
        paramsDto.setCert(DESCR_EXISTS);
        Assertions.assertThrows(ClientException.class, () -> service.updateByParams(paramsDto, 1L));
    }

    @Test
    void checkUpdateByParamsShouldReturnEquals() {
        QueryParams queryParams = getQueryParams();
        QueryParamsDto paramsDtoCreate = getQueryParamsDto();
        Mockito.doReturn(queryParams).when(mapper).convert(paramsDtoCreate);
        GiftCertificate certificate = getCertificate();
        GiftCertificateDto certificateDto = getCertificateDto();
        Mockito.doReturn(certificate).when(certificateBuilder).buildCertificate(queryParams);
        Optional<GiftCertificate> optional = Optional.of(certificate);
        Mockito.doReturn(optional).when(certificateRepository).findById(1L);
        Mockito.doReturn(certificateDto).when(mapper).convert(certificate);
        Mockito.doReturn(certificate).when(certificateRepository).saveAndFlush(certificate);

        GiftCertificateDto actual = service.updateByParams(paramsDtoCreate, 1L);

        assertThat(actual).isEqualTo(certificateDto);
    }

    @Test
    void checkCreateShouldReturnEquals() {
        GiftCertificate certificate = getCertificate();
        GiftCertificateDto certificateDto = getCertificateDto();
        Mockito.doReturn(certificate).when(certificateRepository).save(certificate);
        Mockito.doReturn(certificateDto).when(mapper).convert(certificate);
        Mockito.doReturn(certificate).when(mapper).convert(certificateDto);

        GiftCertificateDto actual = service.create(certificateDto);

        assertThat(actual).isEqualTo(certificateDto);
    }

    @Test
    void checkUpdateShouldReturnEquals() {
        GiftCertificate certificate = getCertificate();
        GiftCertificateDto dto = getCertificateDto();
        Mockito.doReturn(certificate).when(certificateRepository).saveAndFlush(certificate);
        Mockito.doReturn(certificate).when(mapper).convert(dto);
        Mockito.doReturn(dto).when(mapper).convert(certificate);

        GiftCertificateDto actual = service.update(dto);

        assertThat(actual).isEqualTo(dto);
    }

    @Test
    void checkFindAllShouldReturnHasSize5() {
        Pageable pageable = PageRequest.of(0, 5, Direction.ASC, "id");
        List<GiftCertificate> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(getCertificate());
        }
        Page<GiftCertificate> page = new PageImpl<>(list);
        Mockito.doReturn(page).when(certificateRepository).findAll(pageable);

        Page<GiftCertificateDto> actual = service.findAll(pageable);

        assertThat(actual.getSize()).isEqualTo(5);
    }

    @Test
    void checkFindByParamsShouldHasSize2() {
        GiftCertificate certificate = getCertificate();
        GiftCertificateDto certificateDto = getCertificateDto();
        QueryParams queryParams = new QueryParams();
        Specification<GiftCertificate> specification = specificationBuilder.getSpecificationsSelectCertificateByParams(queryParams);
        List<GiftCertificate> list = List.of(certificate, certificate);
        Pageable pageable = PageRequest.of(0, 2, Direction.ASC, "id");
        Mockito.doReturn(pageable).when(pagingUtil).getPageable(queryParams);
        Page<GiftCertificate> page = new PageImpl<>(list);
        Mockito.doReturn(page).when(certificateRepository).findAll(specification, pageable);
        QueryParamsDto paramsDto = new QueryParamsDto();
        Mockito.doReturn(queryParams).when(mapper).convert(paramsDto);
        Mockito.doReturn(certificateDto).when(mapper).convert(certificate);

        List<GiftCertificateDto> actual = service.findByParams(paramsDto);

        assertThat(actual).hasSize(2);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 200})
    void checkFindByIdShouldThrowNotFoundExc(Long id) {
        Mockito.doReturn(Optional.empty()).when(certificateRepository).findById(id);
        Assertions.assertThrows(NotFoundException.class, () -> service.findById(id));
    }

    @Test
    void checkFindByIdShouldReturnEquals() {
        GiftCertificate certificate = getCertificate();
        certificate.setId(1L);
        Optional<GiftCertificate> optional = Optional.of(certificate);
        Mockito.doReturn(optional).when(certificateRepository).findById(1L);
        GiftCertificateDto dto = getCertificateDto();
        dto.setId(1L);
        Mockito.doReturn(dto).when(mapper).convert(certificate);

        GiftCertificateDto actual = service.findById(1L);

        assertThat(actual).isEqualTo(dto);
    }

    @Captor
    ArgumentCaptor<Long> captor;

    @Test
    void checkDeleteShouldCapture() {
        service.delete(1L);
        Mockito.verify(certificateRepository).deleteById(captor.capture());
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
}