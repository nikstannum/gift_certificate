package ru.clevertec.ecl.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.clevertec.ecl.service.dto.OrderDto;

public interface OrderService extends CrudService<OrderDto, Long> {
    /**
     * method returning a paginated list of orders for a given user
     *
     * @param id       user ID
     * @param pageable abstract interface for pagination information
     * @return the paginated list of orders
     */
    Page<OrderDto> findOrdersByUserId(Long id, Pageable pageable);

    /**
     * a method that returns the order of the given user by the identifier of this order. If the specified order ID does not belong to the given
     * user, then an exception is thrown
     *
     * @param userId  ID of this user
     * @param orderId searched order identifier
     * @return the requested order
     */
    OrderDto findOrderByIdByUserId(Long userId, Long orderId);
}
