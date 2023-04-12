package ru.clevertec.ecl.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.Order;
import ru.clevertec.ecl.data.entity.Order.Status;
import ru.clevertec.ecl.data.entity.OrderInfo;
import ru.clevertec.ecl.data.entity.User;
import ru.clevertec.ecl.data.repository.GiftCertificateRepository;
import ru.clevertec.ecl.data.repository.OrderRepository;
import ru.clevertec.ecl.data.repository.UserRepository;
import ru.clevertec.ecl.service.dto.GiftCertificateDto;
import ru.clevertec.ecl.service.dto.OrderCostTimeDto;
import ru.clevertec.ecl.service.dto.OrderDto;
import ru.clevertec.ecl.service.dto.OrderDto.StatusDto;
import ru.clevertec.ecl.service.dto.OrderInfoDto;
import ru.clevertec.ecl.service.dto.UserDto;
import ru.clevertec.ecl.service.exception.NotFoundException;
import ru.clevertec.ecl.service.mapper.Mapper;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private GiftCertificateRepository certificateRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Mapper mapper;
    @InjectMocks
    private OrderServiceImpl service;

    @Test
    void checkFindOrdersByUserIdShouldHasSize2() {
        Pageable pageable = PageRequest.of(0, 2, Direction.ASC, "id");
        List<Order> list = List.of(new Order(), new Order());
        Page<Order> page = new PageImpl<>(list);
        Mockito.doReturn(page).when(orderRepository).findByUserId(1L, pageable);

        Page<OrderDto> pageDto = service.findOrdersByUserId(1L, pageable);

        assertThat(pageDto).hasSize(2);
    }

    @Test
    void checkFindByIdShouldReturnEquals() {
        Order order = new Order();
        order.setId(1L);
        Mockito.doReturn(Optional.of(order)).when(orderRepository).findById(1L);
        OrderDto dto = new OrderDto();
        dto.setId(1L);
        Mockito.doReturn(dto).when(mapper).convert(order);

        OrderDto actual = service.findById(1L);

        assertThat(actual.getId()).isEqualTo(1L);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 200})
    void checkFindByIdShouldThrowNotFoundExc(Long id) {
        Mockito.doReturn(Optional.empty()).when(orderRepository).findById(id);

        Assertions.assertThrows(NotFoundException.class, () -> service.findById(id));
    }

    @Test
    void checkFindOrderByIdByUserIdShouldReturnEquals() {
        User user = new User();
        user.setId(1L);
        Order order = new Order();
        order.setUser(user);
        Mockito.doReturn(Optional.of(order)).when(orderRepository).findById(1L);
        OrderDto dto = new OrderDto();
        dto.setId(1L);
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        dto.setUserDto(userDto);
        Mockito.doReturn(dto).when(mapper).convert(order);

        OrderDto actual = service.findOrderByIdByUserId(1L, 1L);

        assertThat(actual).isEqualTo(dto);
    }

    @Test
    void checkFindOrderByIdByUserIdShouldThrowNotFoundExc() {
        User user = new User();
        user.setId(1L);
        Order order = new Order();
        order.setUser(user);
        Mockito.doReturn(Optional.of(order)).when(orderRepository).findById(1L);

        Assertions.assertThrows(NotFoundException.class, () -> service.findOrderByIdByUserId(2L, 1L));
    }

    @Test
    void findOrderCostTimeByIdByUserId() {
        Instant instant = Instant.now();
        Order order = new Order();
        order.setTotalCost(BigDecimal.valueOf(12.34));
        OrderInfo info = new OrderInfo();
        info.setCreateDate(instant);
        order.setDetails(List.of(info));
        User user = new User();
        user.setId(1L);
        order.setUser(user);
        OrderDto orderDto = new OrderDto();
        orderDto.setTotalCost(BigDecimal.valueOf(12.34));
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        orderDto.setUserDto(userDto);
        OrderInfoDto infoDto = new OrderInfoDto();
        infoDto.setCreateDate(instant);
        orderDto.setDetailsDto(List.of(infoDto));
        Mockito.doReturn(orderDto).when(mapper).convert(order);
        Mockito.doReturn(Optional.of(order)).when(orderRepository).findById(1L);
        OrderCostTimeDto expected = new OrderCostTimeDto();
        expected.setTotalCost(BigDecimal.valueOf(12.34));
        expected.setPurchaseTime(instant);

        OrderCostTimeDto actual = service.findOrderCostTimeByIdByUserId(1L, 1L);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkUpdateShouldReturnEquals() {
        Order order = new Order();
        order.setStatus(Status.CANCELED);
        Mockito.doReturn(order).when(orderRepository).save(order);
        OrderDto dto = new OrderDto();
        dto.setStatusDto(StatusDto.CANCELED);
        Mockito.doReturn(dto).when(mapper).convert(order);
        Mockito.doReturn(order).when(mapper).convert(dto);

        OrderDto actual = service.update(dto);

        assertThat(actual).isEqualTo(dto);
    }

    @Test
    void checkFindAllShouldHasSize2() {
        Pageable pageable = PageRequest.of(0, 2, Direction.ASC, "id");
        List<Order> list = List.of(new Order(), new Order());
        Page<Order> pageOrder = new PageImpl<>(list);
        Mockito.doReturn(pageOrder).when(orderRepository).findAll(pageable);

        Page<OrderDto> page = service.findAll(pageable);

        assertThat(page).hasSize(2);
    }

    @Test
    void checkCreateShouldReturnTotalCostEquals() {
        OrderDto orderDto = prepareOrderDto();
        Order order = prepareOrder();
        Mockito.doReturn(order).when(mapper).convert(orderDto);
        prepareCertRepoMock();
        prepareUserRepoMock();
        Order created = prepareOrder();
        created.setId(1L);
        Mockito.doReturn(created).when(orderRepository).saveAndFlush(order);
        Mockito.doReturn(Optional.of(created)).when(orderRepository).findById(1L);
        OrderDto createdDto = prepareOrderDto();
        createdDto.setTotalCost(BigDecimal.valueOf(7.00));
        Mockito.doReturn(createdDto).when(mapper).convert(created);

        OrderDto actual = service.create(orderDto);

        assertThat(actual.getTotalCost()).isEqualTo(BigDecimal.valueOf(7.00));
    }

    private void prepareCertRepoMock() {
        GiftCertificate cert1 = new GiftCertificate();
        cert1.setPrice(BigDecimal.valueOf(1.00));
        Mockito.doReturn(Optional.of(cert1)).when(certificateRepository).findById(1L);
        GiftCertificate cert2 = new GiftCertificate();
        cert2.setPrice(BigDecimal.valueOf(2.00));
        Mockito.doReturn(Optional.of(cert2)).when(certificateRepository).findById(2L);
    }

    private void prepareUserRepoMock() {
        User user = new User();
        user.setId(1L);
        Mockito.doReturn(Optional.of(user)).when(userRepository).findById(1L);
    }

    private Order prepareOrder() {
        User user = new User();
        user.setId(1L);
        GiftCertificate certificate1 = new GiftCertificate();
        certificate1.setId(1L);
        certificate1.setPrice(BigDecimal.valueOf(1));
        OrderInfo info1 = new OrderInfo();
        info1.setGiftCertificate(certificate1);
        info1.setCertificateQuantity(1);
        OrderInfo info2 = new OrderInfo();
        GiftCertificate certificate2 = new GiftCertificate();
        certificate2.setId(2L);
        certificate2.setPrice(BigDecimal.valueOf(2));
        info2.setGiftCertificate(certificate2);
        info2.setCertificateQuantity(3);
        Order order = new Order();
        order.setUser(user);
        order.setDetails(List.of(info1, info2));
        order.setTotalCost(BigDecimal.valueOf(7.00));
        order.setStatus(Status.PENDING);
        return order;
    }

    private OrderDto prepareOrderDto() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        GiftCertificateDto certificateDto1 = new GiftCertificateDto();
        certificateDto1.setId(1L);
        OrderInfoDto infoDto1 = new OrderInfoDto();
        infoDto1.setGiftCertificateDto(certificateDto1);
        infoDto1.setCertificateQuantity(1);
        OrderInfoDto infoDto2 = new OrderInfoDto();
        GiftCertificateDto certificateDto2 = new GiftCertificateDto();
        certificateDto2.setId(2L);
        infoDto2.setGiftCertificateDto(certificateDto2);
        infoDto2.setCertificateQuantity(3);
        OrderDto orderDto = new OrderDto();
        orderDto.setUserDto(userDto);
        orderDto.setDetailsDto(List.of(infoDto1, infoDto2));
        return orderDto;
    }

    @Test
    void checkDeleteShouldThrowNotFoundException() {
        Mockito.doReturn(Optional.empty()).when(orderRepository).findById(1L);
        Assertions.assertThrows(NotFoundException.class, () -> service.delete(1L));
    }

    @Captor
    ArgumentCaptor<Order> captor;

    @Test
    void checkDeleteShouldCapture() {
        Order order = new Order();
        order.setDeleted(false);
        order.setId(1L);
        Mockito.doReturn(Optional.of(order)).when(orderRepository).findById(1L);

        service.delete(1L);
        Mockito.verify(orderRepository).save(captor.capture());
        Order actual = captor.getValue();

        assertThat(actual.isDeleted()).isEqualTo(true);
    }
}