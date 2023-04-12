package ru.clevertec.ecl.data.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.clevertec.ecl.data.entity.GiftCertificate;

public interface GiftCertificateRepository extends CrudRepository<GiftCertificate, Long>, JpaSpecificationExecutor<GiftCertificate> {
}
