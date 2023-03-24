package ru.clevertec.ecl.data.repository.dao;

import java.util.List;
import java.util.Optional;
import ru.clevertec.ecl.data.entity.Tag;

public interface TagDao extends CrudDao<Tag, Long> {
    List<Tag> findTagsByGiftCertificateId(Long id);

    Optional<Tag> findTagByName(String tagName);
}
