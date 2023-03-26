package ru.clevertec.ecl.data.repository;

import java.util.List;
import java.util.Optional;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.data.entity.Tag;

public interface GiftCertificateRepository extends CrudRepository<GiftCertificate, Long> {
    /**
     * serializes objects in the database, such as GiftCertificate by generated SQL query. GiftCertificate contains a list of tags
     *
     * @param params QueryParams
     * @return serialized GiftCertificate
     */
    GiftCertificate createByParams(QueryParams params);

    /**
     * Updates an object based on the passed parameters
     *
     * @param params QueryParams
     * @param id     GiftCertificate identifier
     * @return GiftCertificate
     */
    GiftCertificate updateByParams(QueryParams params, Long id);

    /**
     * serializes an object from the database by id of GiftCertificate
     *
     * @param id GiftCertificate identifier
     * @return serialized list of tags
     */
    List<Tag> findTagsByCertificateId(Long id);

    /**
     * serializes an object to the database
     *
     * @param tag Tag without identifier
     * @return Tag with identifier
     */
    Tag createTag(Tag tag);

    /**
     * creates a tuple in the join table based on the passed IDs
     *
     * @param certificateId id of GiftCertificate
     * @param tagId         id of Tag
     */
    void createCertificateTagEntry(Long certificateId, Long tagId);

    /**
     * @param name name of tag
     * @return tag packed in Optional or empty Optional
     */
    Optional<Tag> findTagByName(String name);
}
