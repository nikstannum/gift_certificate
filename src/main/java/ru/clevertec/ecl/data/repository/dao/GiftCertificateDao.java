package ru.clevertec.ecl.data.repository.dao;

import java.util.List;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.QueryParams;

public interface GiftCertificateDao extends CrudDao<GiftCertificate, Long> {

    /**
     * serializes a list of objects by parameters, or all objects if there are no parameters. The default is sorted by ID. The size of the list is
     * determined by the parameters. The default list size and maximum size is defined in @{@link ru.clevertec.ecl.data.repository.util.CriteriaQueryBuilder}
     *
     * @param queryParams {@link ru.clevertec.ecl.data.entity.QueryParams} for {@link ru.clevertec.ecl.data.repository.util.CriteriaQueryBuilder}
     * @return serialized list of objects
     */
    List<GiftCertificate> findByParams(QueryParams queryParams);
}
