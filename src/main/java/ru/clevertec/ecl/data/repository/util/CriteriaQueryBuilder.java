package ru.clevertec.ecl.data.repository.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.service.exception.ClientException;

@Component
public class CriteriaQueryBuilder {

    private static final String CODE_CLIENT_BAD_REQUEST = "40000";
    private static final String EXC_PAGE_INT = "page has to integer";
    private static final String EXC_SIZE_INT = "size has to integer";
    private static final String COLUMN_ID = "id";
    private static final String SORT_ASC = "ASC";
    private static final String SORT_DESC = "desc";
    private static final String OP_LIKE = "like";
    private static final String OP_EQ = "eq";
    private static final String COL_DESCRIPTION = "description";
    private static final String ALIAS_DESCR = "descr";
    private static final String CERT_ATTR_TAGS = "tags";
    private static final String PATTERN_PERCENT = "%";
    private static final String COMMA = ",";
    private static final String COLON = ":";
    public static final String ATTR_CREATE_DATE = "createDate";
    public static final String ALIAS_DATE = "date";

    public TypedQuery<GiftCertificate> selectCertificateByParams(EntityManager manager, QueryParams queryParams) {
        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<GiftCertificate> criteriaQuery = cb.createQuery(GiftCertificate.class);
        Root<GiftCertificate> root = criteriaQuery.from(GiftCertificate.class);
        criteriaQuery.select(root);
        List<Predicate> predicates = new ArrayList<>();
        List<List<String>> columnOperationValue = getCertColumnOperationValueSqlSelect(queryParams);
        if (!columnOperationValue.isEmpty()) {
            List<Predicate> certPredicates = getCertPredicates(cb, root, columnOperationValue);
            predicates.addAll(certPredicates);
        }
        List<String> tagOperationValue = getTagColumnOperationValueSelect(queryParams);
        if (!tagOperationValue.isEmpty()) {
            List<Predicate> tagPredicate = getTagPredicate(cb, root, tagOperationValue);
            predicates.addAll(tagPredicate);
        }
        if (!predicates.isEmpty()) {
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));
        }
        List<List<String>> columnOperationOrderList = getColumnOperationOrder(queryParams);
        setSorting(cb, criteriaQuery, root, columnOperationOrderList);
        return manager.createQuery(criteriaQuery)
                .setFirstResult(getOffset(queryParams))
                .setMaxResults(getLimit(queryParams));
    }

    private void setSorting(CriteriaBuilder cb, CriteriaQuery<GiftCertificate> criteriaQuery, Root<GiftCertificate> root,
                            List<List<String>> columnOperationOrderList) {
        List<Order> orders = new ArrayList<>();
        for (List<String> columnOpOrder : columnOperationOrderList) {
            String orderColumn = columnOpOrder.get(0);
            if (orderColumn.equalsIgnoreCase(ALIAS_DATE)) {
                orderColumn = ATTR_CREATE_DATE;
            }
            String sortType = columnOpOrder.get(1);
            Order order;
            if (sortType.equalsIgnoreCase(SORT_DESC)) {
                order = cb.desc(root.get(orderColumn));
            } else {
                order = cb.asc(root.get(orderColumn));
            }
            orders.add(order);
        }
        criteriaQuery.orderBy(orders);
    }

    private List<Predicate> getTagPredicate(CriteriaBuilder cb, Root<GiftCertificate> root, List<String> tagOperationValue) {
        List<Predicate> predicates = new ArrayList<>();
        String column = tagOperationValue.get(0);
        String operation = tagOperationValue.get(1);
        String value = tagOperationValue.get(2);
        Join<GiftCertificate, Tag> join = root.join(CERT_ATTR_TAGS);
        if (operation.equalsIgnoreCase(OP_LIKE)) {
            predicates.add(cb.like(join.get(column), PATTERN_PERCENT + value + PATTERN_PERCENT));
        } else {
            predicates.add(cb.equal(join.get(column), value));
        }
        return predicates;
    }

    private List<Predicate> getCertPredicates(CriteriaBuilder cb, Root<GiftCertificate> root, List<List<String>> columnOperationValue) {
        List<Predicate> certPredicates = new ArrayList<>();
        for (List<String> unit : columnOperationValue) {
            if (unit.get(1).equalsIgnoreCase(OP_LIKE)) {
                certPredicates.add(cb.like(root.get(unit.get(0)), PATTERN_PERCENT + unit.get(2) + PATTERN_PERCENT));
            }
            if (unit.get(1).equalsIgnoreCase(OP_EQ)) {
                certPredicates.add(cb.equal(root.get(unit.get(0)), unit.get(2)));
            }
        }
        return certPredicates;
    }

    private List<List<String>> getCertColumnOperationValueSqlSelect(QueryParams queryParams) {
        String certParams = queryParams.getCert();
        if (certParams == null) {
            return Collections.emptyList();
        }
        String[] certParamsArr = certParams.split(COMMA);
        List<List<String>> listColumnOperationValue = new ArrayList<>();
        for (String param : certParamsArr) {
            String[] paramArr = param.split(COLON);
            String column = paramArr[0];
            if (column.equalsIgnoreCase(ALIAS_DESCR)) {
                column = COL_DESCRIPTION;
            }
            String operation = paramArr[1];
            String value = paramArr[2];
            List<String> listUnitColumnOperationValue = List.of(column, operation, value);
            listColumnOperationValue.add(listUnitColumnOperationValue);
        }
        return listColumnOperationValue;
    }

    private List<String> getTagColumnOperationValueSelect(QueryParams queryParams) {
        String tagParams = queryParams.getTag();
        if (tagParams == null) {
            return Collections.emptyList();
        }
        String[] tagParamsArr = tagParams.split(COLON);
        List<String> listColumnOperationValue = new ArrayList<>();
        listColumnOperationValue.add(tagParamsArr[0]);
        listColumnOperationValue.add(tagParamsArr[1]);
        listColumnOperationValue.add(tagParamsArr[2]);
        return listColumnOperationValue;
    }

    private List<List<String>> getColumnOperationOrder(QueryParams queryParams) {
        String order = queryParams.getOrder();
        if (order == null) {
            List<String> defaultSort = List.of(COLUMN_ID, SORT_ASC);
            return List.of(defaultSort);
        }
        List<List<String>> list = new ArrayList<>();
        String[] orderArr = order.split(COMMA);
        for (String params : orderArr) {
            String[] paramsArr = params.split(COLON);
            String column = paramsArr[0];
            String type = paramsArr[1];
            List<String> paramSort = List.of(column, type);
            list.add(paramSort);
        }
        return list;
    }

    private int getOffset(QueryParams queryParams) {
        int page;
        String pageStr = queryParams.getPage();
        if (pageStr == null)
            return 0;
        try {
            page = Integer.parseInt(queryParams.getPage());
            if (page < 1) {
                page = 1;
            }
        } catch (NumberFormatException e) {
            throw new ClientException(EXC_PAGE_INT, e, CODE_CLIENT_BAD_REQUEST);
        }
        int limit = getLimit(queryParams);
        return (page - 1) * limit;
    }

    private int getLimit(QueryParams queryParams) {
        int limit;
        String limitStr = queryParams.getSize();
        if (limitStr == null) {
            limit = 10;
            return limit;
        }
        try {
            limit = Integer.parseInt(queryParams.getSize());
            if (limit > 50 || limit < 1) {
                limit = 10;
            }
        } catch (NumberFormatException e) {
            throw new ClientException(EXC_SIZE_INT, e, CODE_CLIENT_BAD_REQUEST);
        }
        return limit;
    }
}
