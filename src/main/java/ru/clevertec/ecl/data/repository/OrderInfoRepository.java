package ru.clevertec.ecl.data.repository;

import java.util.List;
import ru.clevertec.ecl.data.entity.OrderInfo;

public interface OrderInfoRepository extends CrudRepository<OrderInfo, Long> {
    /**
     * method to get order details by order ID
     *
     * @param id order ID
     * @return list of order details
     */
    List<OrderInfo> findByOrderId(Long id);
}
