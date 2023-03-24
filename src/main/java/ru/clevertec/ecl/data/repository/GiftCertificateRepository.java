package ru.clevertec.ecl.data.repository;

import java.util.List;
import java.util.Optional;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.data.entity.Tag;

public interface GiftCertificateRepository extends CrudRepository<GiftCertificate, Long> {

    GiftCertificate createByParams(QueryParams params);

    GiftCertificate updateByParams(QueryParams params, Long id);

    List<Tag> findTagsByCertificateId(Long id);

    Tag createTag(Tag tag);

    void createCertificateTagEntry(Long certificateId, Long tagId);

    Optional<Tag> findTagByName(String name);
}
