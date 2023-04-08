package ru.clevertec.ecl.data.repository;

import java.util.List;
import ru.clevertec.ecl.data.entity.OrderInfo;

public interface OrderInfoRepository extends CrudRepository<OrderInfo, Long>{
    List<OrderInfo> findByOrderId(Long id);
}
