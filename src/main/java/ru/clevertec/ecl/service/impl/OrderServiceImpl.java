package ru.clevertec.ecl.service.impl;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.Order;
import ru.clevertec.ecl.data.entity.Order.Status;
import ru.clevertec.ecl.data.entity.OrderInfo;
import ru.clevertec.ecl.data.entity.User;
import ru.clevertec.ecl.data.repository.GiftCertificateRepository;
import ru.clevertec.ecl.data.repository.OrderRepository;
import ru.clevertec.ecl.data.repository.UserRepository;
import ru.clevertec.ecl.service.OrderService;
import ru.clevertec.ecl.service.dto.OrderCostTimeDto;
import ru.clevertec.ecl.service.dto.OrderDto;
import ru.clevertec.ecl.service.exception.NotFoundException;
import ru.clevertec.ecl.service.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final String EXC_MSG_ORDER_NOT_FOUND_ID = "couldn't found order with id = ";
    private static final String CODE_ORDER_READ = "40442";
    private static final String CODE_ORDER_DELETE = "40444";
    public static final String CODE_FORBID_ORDER_READ = "40342";
    public static final String CODE_ORDER_CREATE_NOT_FOUND = "40441";
    public static final String CODE_USER_READ = "40432";
    public static final String EXC_MSG_USER_NOT_FOUND_ID = "wasn't found user with id = ";
    public static final String EXC_MSG_NOT_FOUND_CERT_ID = "wasn't found certificate with id = ";
    public static final String CODE_CERT_READ = "40412";

    private final OrderRepository orderRepository;
    private final GiftCertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    /**
     * returns a paginated list of orders for the given user
     *
     * @param id       user ID
     * @param pageable abstract interface for pagination information
     * @return the paginated list of orders
     */
    @Override
    public Page<OrderDto> findOrdersByUserId(Long id, Pageable pageable) {
        Page<Order> page = orderRepository.findByUserId(id, pageable);
        return page.map(mapper::convert);
    }

    /**
     * returns an order by its id
     *
     * @param id object identifier
     * @return the requested order
     */

    @Override
    public OrderDto findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(EXC_MSG_ORDER_NOT_FOUND_ID, CODE_ORDER_READ));
        return mapper.convert(order);
    }

    /**
     * a method that returns the order of the given user by the identifier of this order. If the specified order ID does not belong to the given
     * user, then an exception is thrown
     *
     * @param userId  ID of this user
     * @param orderId searched order identifier
     * @return the requested order
     */
    @Override
    public OrderDto findOrderByIdByUserId(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(EXC_MSG_ORDER_NOT_FOUND_ID + orderId, CODE_ORDER_READ));
        Long userIdFromDb = order.getUser().getId();
        if (!userIdFromDb.equals(userId)) {
            throw new NotFoundException(EXC_MSG_ORDER_NOT_FOUND_ID + orderId, CODE_FORBID_ORDER_READ);
        }
        return mapper.convert(order);
    }

    @Override
    public OrderCostTimeDto findOrderCostTimeByIdByUserId(Long userId, Long orderId) {
        OrderDto orderDto = findOrderByIdByUserId(userId, orderId);
        OrderCostTimeDto dto = new OrderCostTimeDto();
        dto.setTotalCost(orderDto.getTotalCost());
        dto.setPurchaseTime(orderDto.getDetailsDto().get(0).getCreateDate());
        return dto;
    }

    /**
     * updates an existing order
     *
     * @param dto object for updating
     * @return updated object
     */
    @Override
    @Transactional
    public OrderDto update(OrderDto dto) {
        Order order = mapper.convert(dto);
        return mapper.convert(orderRepository.save(order));
    }

    /**
     * returns a paginated list of orders
     *
     * @param pageable abstract interface for pagination information
     * @return page of objects
     */
    @Override
    public Page<OrderDto> findAll(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        return page.map(mapper::convert);
    }

    /**
     * creates an order and sends it for further serialization to the database
     *
     * @param dto object for creation
     * @return the created order
     */
    @Override
    @Transactional
    public OrderDto create(OrderDto dto) {
        Order order = prepareOrderCreate(dto);
        prepareUser(order);
        prepareCertificate(order);
        Order created = orderRepository.saveAndFlush(order);
        Long id = created.getId();
        return findById(id);
    }

    private void prepareUser(Order order) {
        Long userId = order.getUser().getId();
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(EXC_MSG_USER_NOT_FOUND_ID + userId, CODE_USER_READ));
        order.setUser(user);
    }

    private void prepareCertificate(Order order) {
        List<OrderInfo> details = order.getDetails();
        for (OrderInfo detail : details) {
            Long certId = detail.getGiftCertificate().getId();
            GiftCertificate certificate = certificateRepository.findById(certId)
                    .orElseThrow(() -> new NotFoundException(EXC_MSG_NOT_FOUND_CERT_ID + certId, CODE_CERT_READ));
            detail.setGiftCertificate(certificate);
        }
    }

    private Order prepareOrderCreate(OrderDto dto) {
        Order order = mapper.convert(dto);
        order.setStatus(Status.PENDING);
        List<OrderInfo> details = order.getDetails();
        BigDecimal totalCost = BigDecimal.ZERO;
        for (OrderInfo detail : details) {
            Long certId = detail.getGiftCertificate().getId();
            GiftCertificate cert = certificateRepository.findById(certId)
                    .orElseThrow(() -> new NotFoundException(EXC_MSG_ORDER_NOT_FOUND_ID + certId, CODE_ORDER_CREATE_NOT_FOUND));
            BigDecimal price = cert.getPrice();
            detail.setCertificatePrice(price);
            Integer quantity = detail.getCertificateQuantity();
            price = price.multiply(BigDecimal.valueOf(quantity));
            totalCost = totalCost.add(price);
            detail.setOrder(order);
        }
        order.setTotalCost(totalCost);
        order.setDetails(details);
        return order;
    }

    /**
     * deletes an existing order. If an order with this ID does not exist, an exception will be thrown. Soft delete mechanism implemented
     *
     * @param id object identifier
     */
    @Override
    @Transactional
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(EXC_MSG_ORDER_NOT_FOUND_ID, CODE_ORDER_DELETE));
        order.setDeleted(true);
        orderRepository.save(order);
    }
}
