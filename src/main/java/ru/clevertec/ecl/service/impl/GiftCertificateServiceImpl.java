package ru.clevertec.ecl.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.data.repository.GiftCertificateRepository;
import ru.clevertec.ecl.data.repository.TagRepository;
import ru.clevertec.ecl.service.GiftCertificateService;
import ru.clevertec.ecl.service.dto.GiftCertificateDto;
import ru.clevertec.ecl.service.dto.QueryParamsDto;
import ru.clevertec.ecl.service.exception.ClientException;
import ru.clevertec.ecl.service.exception.NotFoundException;
import ru.clevertec.ecl.service.mapper.Mapper;
import ru.clevertec.ecl.service.util.CertificateBuilder;
import ru.clevertec.ecl.service.util.PagingUtil;
import ru.clevertec.ecl.service.util.SpecificationBuilder;

@Service
@RequiredArgsConstructor
public class GiftCertificateServiceImpl implements GiftCertificateService {

    private static final String EXC_MSG_DESCR_EXISTS = "Certificate with such description already exists";
    private static final String EXC_MSG_NOT_FOUND_ID = "wasn't found certificate with id = ";
    private static final String CODE_CLIENT_CERT_READ = "40412";
    private static final String CODE_CERT_UPD = "40013";
    private static final String COLON = ":";
    private static final String COMMA = ",";
    private static final String ALIAS_DESCR = "descr";
    private static final String OP_EQ = "eq";

    private final GiftCertificateRepository certificateRepository;
    private final TagRepository tagRepository;
    private final CertificateBuilder certificateBuilder;
    private final SpecificationBuilder specificationBuilder;
    private final PagingUtil pagingUtil;
    private final Mapper mapper;

    private List<Tag> getTagsForCreation(List<Tag> acceptedTags) {
        if (acceptedTags.isEmpty()) {
            return Collections.emptyList();
        }
        List<Tag> tagsForCreation = new ArrayList<>();
        for (Tag acceptedTag : acceptedTags) {
            Optional<Tag> tagFromDb = tagRepository.findByName(acceptedTag.getName());
            if (tagFromDb.isEmpty()) {
                tagsForCreation.add(acceptedTag);
            }
        }
        return tagsForCreation;
    }

    @Override
    @Transactional
    public GiftCertificateDto createByParams(QueryParamsDto paramsDto) {
        QueryParams params = mapper.convert(paramsDto);
        if (isExistsWithSuchDescription(params)) {
            throw new ClientException(EXC_MSG_DESCR_EXISTS, CODE_CERT_UPD);
        }
        GiftCertificate certificateForCreation = certificateBuilder.buildCertificate(params);
        List<Tag> existingTags = getExistingTags(certificateForCreation.getTags());
        List<Tag> tagsForCreation = getTagsForCreation(certificateForCreation.getTags());
        List<Tag> createdTags = new ArrayList<>();
        for (Tag tag : tagsForCreation) {
            createdTags.add(tagRepository.save(tag));
        }
        List<Tag> tags = new ArrayList<>();
        if (!existingTags.isEmpty()) {
            tags.addAll(existingTags);
        }
        if (!createdTags.isEmpty()) {
            tags.addAll(createdTags);
        }
        certificateForCreation.setTags(tags);
        GiftCertificate created = certificateRepository.save(certificateForCreation);
        return mapper.convert(created);
    }


    private String getParamCertDescr(QueryParams queryParams) {
        String allCertParams = queryParams.getCert();
        if (allCertParams != null) {
            String[] arrParams = allCertParams.split(COMMA);
            for (String param : arrParams) {
                if (param.toLowerCase().startsWith(ALIAS_DESCR)) {
                    return param.split(COLON)[1];
                }
            }
        }
        return null;
    }

    private boolean isExistsWithSuchDescription(QueryParams params) {
        String descrVal = getParamCertDescr(params);
        if (descrVal == null) {
            return false;
        }
        String descr = ALIAS_DESCR + COLON + OP_EQ + COLON + descrVal;
        QueryParams queryParams = new QueryParams();
        queryParams.setCert(descr);
        Specification<GiftCertificate> specification = specificationBuilder.getSpecifications(queryParams);
        List<GiftCertificate> list = certificateRepository.findAll(specification);
        return !list.isEmpty();
    }

    private void updateFields(GiftCertificate fromDb, GiftCertificate certWithAcceptedParams) {
        if (certWithAcceptedParams.getName() != null) {
            fromDb.setName(certWithAcceptedParams.getName());
        }
        if (certWithAcceptedParams.getDescription() != null) {
            fromDb.setDescription(certWithAcceptedParams.getDescription());
        }
        if (certWithAcceptedParams.getPrice() != null) {
            fromDb.setPrice(certWithAcceptedParams.getPrice());
        }
        if (certWithAcceptedParams.getDuration() != null) {
            fromDb.setDuration(certWithAcceptedParams.getDuration());
        }
    }

    @Override
    @Transactional
    public GiftCertificateDto update(GiftCertificateDto dto) {
        GiftCertificate entity = mapper.convert(dto);
        return mapper.convert(certificateRepository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public GiftCertificateDto create(GiftCertificateDto dto) {
        GiftCertificate entity = mapper.convert(dto);
        return mapper.convert(certificateRepository.save(entity));
    }

    @Override
    @Transactional
    public GiftCertificateDto updateByParams(QueryParamsDto paramsDto, Long id) {
        QueryParams params = mapper.convert(paramsDto);
        if (isExistsWithSuchDescription(params)) {
            throw new ClientException(EXC_MSG_DESCR_EXISTS, CODE_CERT_UPD);
        }
        GiftCertificate certWithAcceptedParams = certificateBuilder.buildCertificate(params);
        List<Tag> existingTags = getExistingTags(certWithAcceptedParams.getTags());
        List<Tag> tagsForCreation = getTagsForCreation(certWithAcceptedParams.getTags());
        List<Tag> createdTags = new ArrayList<>();
        for (Tag tag : tagsForCreation) {
            createdTags.add(tagRepository.save(tag));
        }
        List<Tag> tags = new ArrayList<>();
        if (!existingTags.isEmpty()) {
            tags.addAll(existingTags);
        }
        if (!createdTags.isEmpty()) {
            tags.addAll(createdTags);
        }
        certWithAcceptedParams.setTags(tags);
        certWithAcceptedParams.setId(id);
        GiftCertificate fromDb = certificateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(EXC_MSG_NOT_FOUND_ID + id, CODE_CLIENT_CERT_READ));
        updateFields(fromDb, certWithAcceptedParams);
        GiftCertificate updated = certificateRepository.saveAndFlush(fromDb);
        return mapper.convert(updated);
    }

    private List<Tag> getExistingTags(List<Tag> tags) {
        if (tags.isEmpty()) {
            return Collections.emptyList();
        }
        List<Tag> existingTags = new ArrayList<>();
        for (Tag tag : tags) {
            Optional<Tag> optionalTag = tagRepository.findByName(tag.getName());
            optionalTag.ifPresent(existingTags::add);
        }
        return existingTags;
    }


    @Override
    public Page<GiftCertificateDto> findAll(Pageable pageable) {
        Page<GiftCertificate> page = certificateRepository.findAll(pageable);
        return page.map(mapper::convert);
    }

    @Override
    public List<GiftCertificateDto> findByParams(QueryParamsDto paramsDto) {
        QueryParams params = mapper.convert(paramsDto);
        Specification<GiftCertificate> specification = specificationBuilder.getSpecifications(params);
        Pageable pageable = pagingUtil.getPageable(params);
        Page<GiftCertificate> certificates = certificateRepository.findAll(specification, pageable);
        return certificates.stream().map(mapper::convert).toList();
    }

    @Override
    public GiftCertificateDto findById(Long id) {
        GiftCertificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(EXC_MSG_NOT_FOUND_ID + id, CODE_CLIENT_CERT_READ));
        return mapper.convert(certificate);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        certificateRepository.deleteById(id);
    }
}
