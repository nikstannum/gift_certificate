package ru.clevertec.ecl.data.repository.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.service.exception.ClientException;

@Component
public class QueryBuilder {

    public static final int DECIMAL_SCALE = 2;
    public static final String EXC_MSG_PRICE_MORE_TWO_DIGITS = "Price accuracy should be no more than two digits";
    public static final String CODE_CLIENT_CERT_CREATE = "40011";
    public static final String EXC_MSG_INCORRECT_PRICE_VALUE = "Incorrect price value";
    public static final String CODE_CLIENT_CERT_UPD = "40013";
    public static final String EXC_MSG_DURATION_INTEGER = "duration must be an integer";
    /**
     * List size if this parameter is not specified or exceeds the maximum size
     */
    public static final int DEFAULT_LIST_SIZE = 10;
    public static final int MAX_LIST_SIZE = 50;


    public String buildQueryCertificateUpdate(QueryParams queryParams) {
        return """
                UPDATE gift_certificate
                SET
                """ +
                addCertificateParamsUpdate(queryParams);
    }

    private StringBuilder addCertificateParamsUpdate(QueryParams queryParams) {
        StringBuilder query = new StringBuilder();
        String certParams = queryParams.getCert();
        if (certParams == null) {
            return query;
        }
        String[] arrCertParams = certParams.split(",");
        for (int i = 0; i < arrCertParams.length; i++) {
            String param = arrCertParams[i];
            String[] paramArr = param.split(":");
            String columnParam = paramArr[0];
            String value = paramArr[1];
            String column = getColumn(columnParam);
            query.append(column).append("= ");
            if (columnParam.equals("name") || columnParam.equals("descr")) {
                query.append("'").append(value).append("'");
            } else if (columnParam.equals("price")) {
                BigDecimal valBigDec;
                try {
                    valBigDec = new BigDecimal(value);
                } catch (NumberFormatException e) {
                    throw new ClientException(EXC_MSG_INCORRECT_PRICE_VALUE, CODE_CLIENT_CERT_UPD);
                }
                if (valBigDec.scale() > DECIMAL_SCALE) {
                    throw new ClientException(EXC_MSG_PRICE_MORE_TWO_DIGITS, CODE_CLIENT_CERT_UPD);
                }
                query.append(valBigDec);
            } else if (columnParam.equalsIgnoreCase("duration")) {
                Integer duration;
                try {
                    duration = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new ClientException(EXC_MSG_DURATION_INTEGER, CODE_CLIENT_CERT_UPD);
                }
                query.append(duration);
            }
            if ((arrCertParams.length - 1) != i) {
                query.append(", ");
            }
        }
        query.append(" WHERE id = :id");
        return query;
    }

    private String getColumn(String colomnParam) {
        String column;
        switch (colomnParam) {
            case "descr" -> column = "description ";
            case "name" -> column = "\"name\" ";
            case "price" -> column = "price ";
            case "duration" -> column = "duration ";
            default -> column = " ";
        }
        return column;
    }

    public String buildQueryCertificateCreate(QueryParams queryParams) {
        return """
                INSERT INTO gift_certificate ("name", description, price, duration)
                VALUES
                """ +
                addCertificateParamsOrdered(queryParams);
    }

    private StringBuilder addCertificateParamsOrdered(QueryParams queryParams) {
        StringBuilder query = new StringBuilder();
        String certParams = queryParams.getCert();
        String[] paramsArr = certParams.split(",");
        Map<Integer, String> map = fillMapParams(paramsArr);
        return collectQuery(map, query);
    }

    private StringBuilder collectQuery(Map<Integer, String> map, StringBuilder query) {
        String name = map.get(1);
        String description = map.get(2);
        BigDecimal price = new BigDecimal(map.get(3));
        if (price.scale() > DECIMAL_SCALE) {
            throw new ClientException(EXC_MSG_PRICE_MORE_TWO_DIGITS, CODE_CLIENT_CERT_CREATE);
        }
        Integer duration = Integer.valueOf(map.get(4));
        query.append("('")
                .append(name)
                .append("', ")
                .append("'")
                .append(description)
                .append("', ")
                .append(price)
                .append(", ")
                .append(duration)
                .append(")");
        return query;
    }

    private Map<Integer, String> fillMapParams(String[] paramsArr) {
        Map<Integer, String> map = new HashMap<>();
        for (String s : paramsArr) {
            String[] elm = s.split(":");
            String column = elm[0];
            switch (column) {
                case "name" -> map.put(1, elm[1]);
                case "descr" -> map.put(2, elm[1]);
                case "price" -> map.put(3, elm[1]);
                case "duration" -> map.put(4, elm[1]);
            }
        }
        return map;
    }


    public String buildQuerySelect(QueryParams queryParams) {
        return """
                SELECT g.id, g."name", g.description, g.price, g.duration, g.create_date , g.last_update_date
                """ +
                addFrom(queryParams) +
                addJoin(queryParams) +
                addWhere(queryParams) +
                addTagFiltrationCondition(queryParams) +
                addParamsCertificateFiltrationCondition(queryParams) +
                buildOrder(queryParams) +
                applyPaging(queryParams);
    }

    private int getLimit(String sizeStr) {
        if (sizeStr == null) {
            return DEFAULT_LIST_SIZE;
        }
        int size;
        try {
            size = Integer.parseInt(sizeStr);
        } catch (NumberFormatException e) {
            return DEFAULT_LIST_SIZE;
        }
        if (size > MAX_LIST_SIZE) {
            return DEFAULT_LIST_SIZE;
        }
        return size;
    }

    private StringBuilder applyPaging(QueryParams queryParams) {
        String sizeStr = queryParams.getSize();
        int limit = getLimit(sizeStr);
        String pageStr = queryParams.getPage();
        long offset = getOffset(pageStr, limit);
        return new StringBuilder("LIMIT " + limit + " OFFSET " + offset);
    }

    private long getOffset(String pageStr, int limit) {
        if (pageStr == null) {
            return 0L;
        }
        int page;
        try {
            page = Integer.parseInt(pageStr);
        } catch (NumberFormatException e) {
            page = 1;
        }
        if (page < 1) {
            page = 1;
        }
        return (long) (page - 1) * limit;

    }

    private StringBuilder addFrom(QueryParams params) {
        StringBuilder query = new StringBuilder();
        if (params.getTag() != null) {
            query.append(", t.id AS t_id, t.\"name\" AS t_name ");
        }
        query.append("FROM gift_certificate g ");
        return query;
    }

    private StringBuilder addJoin(QueryParams queryParams) {
        String paramsTag = queryParams.getTag();
        StringBuilder filtration = new StringBuilder();
        if (paramsTag == null) {
            return filtration;
        }
        filtration.append("JOIN certificate_tag ct ON g.id = ct.certificate_id JOIN tag t ON ct.tag_id = t.id ");
        return filtration;
    }

    private StringBuilder addWhere(QueryParams params) {
        StringBuilder where = new StringBuilder();
        if (params.getCert() != null || params.getTag() != null) {
            where.append("WHERE ");
        }
        return where;
    }


    private StringBuilder addTagFiltrationCondition(QueryParams queryParams) {
        StringBuilder filtration = new StringBuilder();
        String paramsTag = queryParams.getTag();
        if (paramsTag == null) {
            return filtration;
        }
        String[] elmArr = paramsTag.split(":");
        String column = elmArr[0];
        String operation = elmArr[1];
        String value = elmArr[2];
        if (column.equals("name")) {
            filtration.append("t.\"name\" ");
        }
        if (operation.equals("eq")) {
            filtration.append("='").append(value).append("'");
        }
        if (operation.equals("like")) {
            filtration.append("LIKE '%").append(value).append("%' ");
        }
        if (queryParams.getCert() != null) {
            filtration.append(" AND ");
        }
        return filtration;
    }

    private StringBuilder addParamsCertificateFiltrationCondition(QueryParams queryParams) {
        String params = queryParams.getCert();
        StringBuilder filtration = new StringBuilder();
        if (params == null) {
            return filtration;
        }
        String[] paramsArr = params.split(",");
        for (int i = 0; i < paramsArr.length; i++) {
            String[] elmArray = paramsArr[i].split(":");
            filtration.append(addColumnFiltration(elmArray)).
                    append(applyOperationFiltration(elmArray));
            if ((paramsArr.length - 1) != i) {
                filtration.append(" AND ");
            }
        }
        return filtration;
    }

    private StringBuilder addColumnFiltration(String[] elmArray) {
        StringBuilder columnFiltration = new StringBuilder();
        switch (elmArray[0]) {
            case "name" -> columnFiltration.append("g.\"name\" ");
            case "descr" -> columnFiltration.append("g.description ");
        }
        return columnFiltration;
    }

    private StringBuilder applyOperationFiltration(String[] elmArray) {
        StringBuilder operationFiltration = new StringBuilder();
        switch (elmArray[1]) {
            case "like" -> operationFiltration.append("LIKE '%").append(elmArray[2]).append("%' ");
            case "eq" -> operationFiltration.append("='").append(elmArray[2]).append("'");
        }
        return operationFiltration;
    }


    private StringBuilder buildOrder(QueryParams queryParamsRead) {
        String orderParams = queryParamsRead.getOrder();
        StringBuilder sorting = new StringBuilder();
        sorting.append("ORDER BY ");
        if (orderParams == null) {
            sorting.append("g.id ");
            return sorting;
        }
        String[] paramsOrder = orderParams.split(",");
        for (int i = 0; i < paramsOrder.length; i++) {
            String[] elmArr = paramsOrder[i].split(":");
            sorting.append(addParamsSort(elmArr));
            sorting.append(applyOperationSort(elmArr));
            if ((paramsOrder.length - 1) != i) {
                sorting.append(", ");
            }
        }
        return sorting;
    }

    private StringBuilder applyOperationSort(String[] elmArr) {
        StringBuilder operation = new StringBuilder();
        if ("desc".equals(elmArr[1])) {
            operation.append("DESC ");
        } else {
            operation.append("ASC ");
        }
        return operation;
    }

    private StringBuilder addParamsSort(String[] elmArr) {
        StringBuilder sortType = new StringBuilder();
        switch (elmArr[0]) {
            case "date" -> sortType.append("g.create_date ");
            case "name" -> sortType.append("g.\"name\" ");
        }
        return sortType;
    }
}
