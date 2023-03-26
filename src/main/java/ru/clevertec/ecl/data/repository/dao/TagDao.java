package ru.clevertec.ecl.data.repository.dao;

import java.util.List;
import java.util.Optional;
import ru.clevertec.ecl.data.entity.Tag;

public interface TagDao extends CrudDao<Tag, Long> {
    /**
     * serializes an object from the database by id of GiftCertificate
     *
     * @param id id of GiftCertificate
     * @return serialized list of objects
     */
    List<Tag> findTagsByGiftCertificateId(Long id);

    /**
     * @param tagName name of tag
     * @return tag packed in Optional or empty Optional
     */
    Optional<Tag> findTagByName(String tagName);
}
