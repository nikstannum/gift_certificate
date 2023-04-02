package ru.clevertec.ecl.data.repository;

import java.util.Optional;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.Tag;

public interface GiftCertificateRepository extends CrudRepository<GiftCertificate, Long> {

    /**
     * serializes an object to the database
     *
     * @param tag Tag without identifier
     * @return Tag with identifier
     */
    Tag createTag(Tag tag);

    /**
     * @param name name of tag
     * @return tag packed in Optional or empty Optional
     */
    Optional<Tag> findTagByName(String name);
}
