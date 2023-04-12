package ru.clevertec.ecl.service;

import ru.clevertec.ecl.service.dto.TagDto;

public interface TagService extends CrudService<TagDto, Long> {
    /**
     * method to get from repository the most widely used tag with the highest order value for this user
     *
     * @param userId user ID
     * @return the most widely used tag for this user
     */
    TagDto findPopularTag(Long userId);
}
