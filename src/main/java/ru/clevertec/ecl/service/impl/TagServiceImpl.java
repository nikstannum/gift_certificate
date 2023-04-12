package ru.clevertec.ecl.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.data.repository.TagRepository;
import ru.clevertec.ecl.service.TagService;
import ru.clevertec.ecl.service.dto.TagDto;
import ru.clevertec.ecl.service.exception.NotFoundException;
import ru.clevertec.ecl.service.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private static final String EXC_MSG_NOT_FOUND_ID = "couldn't find tag with id = ";
    private static final String CODE_TAG_READ = "40422";
    private final TagRepository tagRepository;
    private final Mapper mapper;

    /**
     * method to get from repository the most widely used tag with the highest order value for this user
     *
     * @param userId user ID
     * @return the most widely used tag for this user
     */
    @Override
    public TagDto findPopularTag(Long userId) {
        return mapper.convert(tagRepository.findMostPopularTag(userId));
    }

    @Override
    public TagDto findById(Long id) {
        return mapper.convert(tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(EXC_MSG_NOT_FOUND_ID + id, CODE_TAG_READ)));
    }

    @Override
    @Transactional
    public TagDto update(TagDto dto) {
        Tag tag = mapper.convert(dto);
        return mapper.convert(tagRepository.save(tag));
    }

    @Override
    public Page<TagDto> findAll(Pageable pageable) {
        Page<Tag> page = tagRepository.findAll(pageable);
        return page.map(mapper::convert);
    }

    @Override
    @Transactional
    public TagDto create(TagDto dto) {
        Tag tag = mapper.convert(dto);
        return mapper.convert(tagRepository.save(tag));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        tagRepository.deleteById(id);
    }
}
