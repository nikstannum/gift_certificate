package ru.clevertec.ecl.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.data.repository.GiftCertificateRepository;
import ru.clevertec.ecl.service.GiftCertificateService;
import ru.clevertec.ecl.service.dto.GiftCertificateDto;
import ru.clevertec.ecl.service.dto.QueryParamsDto;
import ru.clevertec.ecl.service.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class GiftCertificateServiceImpl implements GiftCertificateService {

    private final GiftCertificateRepository giftCertificateRepository;
    private final Mapper mapper;

    private List<Tag> getTagsForCreation(List<Tag> existingTags, String tagsParams) {
        String[] acceptedTagsParamsArr = tagsParams.split(",");
        List<Tag> tagsForCreation = new ArrayList<>();
        if (existingTags.isEmpty()) {
            for (String acceptedTagParam : acceptedTagsParamsArr) {
                String acceptedTagName = acceptedTagParam.split(":")[1];
                tagsForCreation.add(getTagForCreation(acceptedTagName));
            }
        } else {
            List<String> existingTagsName = existingTags.stream().map(Tag::getName).toList();
            for (String acceptedTagParam : acceptedTagsParamsArr) {
                String acceptedTagName = acceptedTagParam.split(":")[1];
                if (!existingTagsName.contains(acceptedTagName)) {
                    tagsForCreation.add(getTagForCreation(acceptedTagName));
                }
            }
        }
        return tagsForCreation;
    }

    private Tag getTagForCreation(String acceptedTagName) {
        Tag tag = new Tag();
        tag.setName(acceptedTagName);
        return tag;
    }

    private void createCertificateTagEntries(Long id, List<Tag> createdTags) {
        List<Long> createdTagsId = createdTags.stream().map(Tag::getId).toList();
        for (Long tagId : createdTagsId) {
            giftCertificateRepository.createCertificateTagEntry(id, tagId);
        }
    }

    @Override
    @Transactional
    public GiftCertificateDto create(QueryParamsDto paramsDto) {
        QueryParams params = mapper.convert(paramsDto);
        GiftCertificate created = giftCertificateRepository.createByParams(params);
        String tagsParam = params.getTag();
        if (tagsParam != null) {
            List<Tag> tagsForCreation = getTagsForCreation(Collections.emptyList(), tagsParam);
            List<Tag> createdTags = addMissingTags(tagsForCreation);
            createCertificateTagEntries(created.getId(), createdTags);
            created.setTags(createdTags);
        }
        return mapper.convert(created);
    }

    @Override
    @Transactional
    public GiftCertificateDto update(QueryParamsDto paramsDto, Long id) {
        List<Tag> existingTags = giftCertificateRepository.findTagsByCertificateId(id);
        String acceptedTagsParams = paramsDto.getTag();
        if (acceptedTagsParams != null) {
            List<Tag> tagsForCreation = getTagsForCreation(existingTags, acceptedTagsParams);
            List<Tag> createdTags = addMissingTags(tagsForCreation);
            existingTags.addAll(createdTags);
            createCertificateTagEntries(id, createdTags);
        }
        QueryParams params = mapper.convert(paramsDto);
        GiftCertificate updated = giftCertificateRepository.updateByParams(params, id);
        updated.setTags(existingTags);
        return mapper.convert(updated);
    }

    private List<Tag> addMissingTags(List<Tag> tagsForCreation) {
        List<Tag> addedTags = new ArrayList<>();
        for (Tag tag : tagsForCreation) {
            Optional<Tag> existingTagOpt = giftCertificateRepository.findTagByName(tag.getName());
            if (existingTagOpt.isPresent()) {
                addedTags.add(existingTagOpt.get());
            } else {
                Tag createdTag = giftCertificateRepository.createTag(tag);
                addedTags.add(createdTag);
            }
        }
        return addedTags;
    }

    @Override
    public List<GiftCertificateDto> find(QueryParamsDto paramsDto) {
        QueryParams params = mapper.convert(paramsDto);
        List<GiftCertificate> certificates = giftCertificateRepository.find(params);
        Map<Long, GiftCertificate> resMap = new LinkedHashMap<>();
        certificates.forEach(cert -> resMap.merge(cert.getId(), cert, (a, b) -> {
            b.getTags().addAll(a.getTags());
            return b;
        }));
        List<GiftCertificate> resultList = resMap.values().stream().toList();
        return resultList.stream().map(mapper::convert).toList();
    }


    @Override
    public GiftCertificateDto findById(Long id) {
        return mapper.convert(giftCertificateRepository.findById(id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        giftCertificateRepository.delete(id);
    }
}
