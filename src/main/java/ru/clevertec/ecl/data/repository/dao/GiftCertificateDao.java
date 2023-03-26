package ru.clevertec.ecl.data.repository.dao;

import java.util.List;
import ru.clevertec.ecl.data.entity.GiftCertificate;

public interface GiftCertificateDao extends CrudDao<GiftCertificate, Long> {

    /**
     * serializes objects in the database, such as GiftCertificate by generated SQL query. GiftCertificate contains a list of tags
     *
     * @param query SQL query
     * @return serialized list of objects
     */
    List<GiftCertificate> findByRichParams(String query);

    /**
     * creates an object in the database based on the generated query
     *
     * @param query SQL query
     * @return serialized GiftCertificate
     */
    GiftCertificate createByParams(String query);

    /**
     * creates a tuple in the join table based on the passed IDs
     *
     * @param certificateId id of GiftCertificate
     * @param tagId         id of Tag
     */
    void createCertificateTagEntry(Long certificateId, Long tagId);

    /**
     * removes a tuple in the join table based on the passed ids
     *
     * @param id id of GiftCertificate
     */
    void deleteCertificateTagByCertificateId(Long id);

    /**
     * updates an object in the database by generated SQL query
     *
     * @param query SQL query
     * @param id    id of GiftCertificate
     * @return updated object
     */
    GiftCertificate updateByParams(String query, Long id);
}
