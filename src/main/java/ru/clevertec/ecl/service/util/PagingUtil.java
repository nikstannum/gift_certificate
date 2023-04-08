package ru.clevertec.ecl.service.util;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.service.exception.ClientException;

@Component
public class PagingUtil {

    public static final String ATTR_CREATE_DATE = "createDate";
    public static final String ALIAS_DATE = "date";
    private static final String CODE_CLIENT_BAD_REQUEST = "40000";
    private static final String EXC_PAGE_INT = "page has to integer";
    private static final String EXC_SIZE_INT = "size has to integer";
    private static final String COLUMN_ID = "id";
    private static final String SORT_DESC = "desc";
    private static final String COMMA = ",";
    private static final String COLON = ":";
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 50;
    public static final int DEFAULT_PAGE_NUMBER = 1;

    public Pageable getPageable(QueryParams queryParams) {
        int page = getPage(queryParams);
        int size = getSize(queryParams);
        Sort sort = getSort(queryParams);
        return PageRequest.of(page - DEFAULT_PAGE_NUMBER, size, sort);
    }

    private int getPage(QueryParams queryParams) {
        int page;
        String pageStr = queryParams.getPage();
        if (pageStr == null)
            return DEFAULT_PAGE_NUMBER;
        try {
            page = Integer.parseInt(queryParams.getPage());
            if (page < DEFAULT_PAGE_NUMBER) {
                page = DEFAULT_PAGE_NUMBER;
            }
        } catch (NumberFormatException e) {
            throw new ClientException(EXC_PAGE_INT, e, CODE_CLIENT_BAD_REQUEST);
        }
        return page;
    }

    private int getSize(QueryParams queryParams) {
        int size;
        String sizeStr = queryParams.getSize();
        if (sizeStr == null) {
            return DEFAULT_PAGE_SIZE;
        }
        try {
            size = Integer.parseInt(sizeStr);
            if (size > MAX_PAGE_SIZE || size < DEFAULT_PAGE_NUMBER) {
                size = DEFAULT_PAGE_SIZE;
            }
        } catch (NumberFormatException e) {
            throw new ClientException(EXC_SIZE_INT, e, CODE_CLIENT_BAD_REQUEST);
        }
        return size;
    }

    private Sort getSort(QueryParams queryParams) {
        String orderStrParams = queryParams.getOrder();
        if (orderStrParams == null) {
            return Sort.by(Direction.ASC, COLUMN_ID);
        }
        String[] orderArrParams = orderStrParams.split(COMMA);
        List<Order> orderList = new ArrayList<>();
        for (String param : orderArrParams) {
            String[] paramArr = param.split(COLON);
            String column = paramArr[0];
            String typeStr = paramArr[DEFAULT_PAGE_NUMBER];
            Direction direction;
            if (typeStr.equalsIgnoreCase(SORT_DESC)) {
                direction = Direction.DESC;
            } else {
                direction = Direction.ASC;
            }
            if (column.equalsIgnoreCase(ALIAS_DATE)) {
                column = ATTR_CREATE_DATE;
            }
            Order order = new Order(direction, column);
            orderList.add(order);
        }
        return Sort.by(orderList);
    }
}
