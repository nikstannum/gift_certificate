package ru.clevertec.ecl.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.clevertec.ecl.data.entity.Order;

public interface OrderRepository extends CrudRepository<Order, Long> {
    /**
     * method returning a paginated list of orders by user ID
     *
     * @param id       user ID
     * @param pageable abstract interface for pagination information
     * @return page of orders
     */
    Page<Order> findByUserId(Long id, Pageable pageable);
}
