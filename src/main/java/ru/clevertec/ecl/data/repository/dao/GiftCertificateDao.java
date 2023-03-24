package ru.clevertec.ecl.data.repository.dao;

import java.util.List;
import ru.clevertec.ecl.data.entity.GiftCertificate;

public interface GiftCertificateDao extends CrudDao<GiftCertificate, Long> {
    List<GiftCertificate> findByRichParams(String query);

    GiftCertificate createByParams(String query);

    void createCertificateTagEntry(Long certificateId, Long tagId);

    void deleteCertificateTagByCertificateId(Long id);

    GiftCertificate updateByParams(String query, Long id);
}
