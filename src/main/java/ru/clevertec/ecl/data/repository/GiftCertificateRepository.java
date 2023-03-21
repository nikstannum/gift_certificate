package ru.clevertec.ecl.data.repository;

import java.util.List;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.Tag;

public interface GiftCertificateRepository extends CrudRepository<GiftCertificate, Long> {
    List<GiftCertificate> findByTagName(Tag tag);
    List<GiftCertificate> findByName(String partName);
    List<GiftCertificate> findByDescription(String partDescription);
}
