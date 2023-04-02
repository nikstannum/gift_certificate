package ru.clevertec.ecl.data.repository.dao;

import java.util.Optional;
import ru.clevertec.ecl.data.entity.Tag;

public interface TagDao extends CrudDao<Tag, Long> {

    /**
     * @param tagName name of tag
     * @return tag packed in Optional or empty Optional
     */
    Optional<Tag> findTagByName(String tagName);
}
