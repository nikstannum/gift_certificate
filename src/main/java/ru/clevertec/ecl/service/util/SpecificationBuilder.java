package ru.clevertec.ecl.service.util;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.data.entity.Tag;

@Component
public class SpecificationBuilder {

    private static final String COL_DESCRIPTION = "description";
    private static final String CERT_ATTR_TAGS = "tags";
    private static final String PATTERN_PERCENT = "%";
    private static final String COMMA = ",";
    private static final String COLON = ":";
    public static final String NAME_LIKE = "name:like:";
    public static final String NAME_EQ = "name:eq:";
    public static final String ATTRIBUTE_NAME = "name";
    public static final String DESCR_LIKE = "descr:like:";
    public static final String DESCR_EQ = "descr:eq:";

    public Specification<GiftCertificate> getSpecifications(QueryParams queryParams) {
        Specification<GiftCertificate> specCertName = getCertNameSpec(queryParams);
        Specification<GiftCertificate> specCertDescr = getCertDescrSpec(queryParams);
        Specification<GiftCertificate> specTagName = getTagSpec(queryParams);
        return specCertName.and(specCertDescr).and(specTagName);
    }

    private Specification<GiftCertificate> getCertNameSpec(QueryParams queryParams) {
        return (root, query, cb) -> {
            String paramsCert = queryParams.getCert();
            if (paramsCert == null) {
                return null;
            }
            String[] paramsArr = paramsCert.split(COMMA);
            for (String param : paramsArr) {
                String value = param.split(COLON)[2];
                if (param.startsWith(NAME_LIKE)) {
                    return cb.like(root.get(ATTRIBUTE_NAME), PATTERN_PERCENT + value + PATTERN_PERCENT);
                }
                if (param.startsWith(NAME_EQ)) {
                    return cb.equal(root.get(ATTRIBUTE_NAME), value);
                }
            }
            return null;
        };
    }

    private Specification<GiftCertificate> getCertDescrSpec(QueryParams queryParams) {
        return (root, query, cb) -> {
            String paramsCert = queryParams.getCert();
            if (paramsCert == null) {
                return null;
            }
            String[] paramsArr = paramsCert.split(COMMA);
            for (String param : paramsArr) {
                String value = param.split(COLON)[2];
                if (param.startsWith(DESCR_LIKE)) {
                    return cb.like(root.get(COL_DESCRIPTION), PATTERN_PERCENT + value + PATTERN_PERCENT);
                }
                if (param.startsWith(DESCR_EQ)) {
                    return cb.equal(root.get(COL_DESCRIPTION), value);
                }
            }
            return null;
        };
    }

    private Specification<GiftCertificate> getTagSpec(QueryParams queryParams) {
        return (root, query, cb) -> {
            String params = queryParams.getTag();
            if (params == null) {
                return null;
            }
            String value = params.split(COLON)[2];
            Join<GiftCertificate, Tag> join = root.join(CERT_ATTR_TAGS);
            if (params.startsWith(NAME_LIKE)) {
                return cb.like(join.get(ATTRIBUTE_NAME), PATTERN_PERCENT + value + PATTERN_PERCENT);
            }
            if (params.startsWith(NAME_EQ)) {
                return cb.equal(join.get(ATTRIBUTE_NAME), value);
            }
            return null;
        };
    }
}
