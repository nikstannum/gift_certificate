package ru.clevertec.ecl.integration.service;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import ru.clevertec.ecl.integration.BaseIntegrationTest;
import ru.clevertec.ecl.service.dto.GiftCertificateDto;
import ru.clevertec.ecl.service.dto.OrderDto;
import ru.clevertec.ecl.service.dto.OrderDto.StatusDto;
import ru.clevertec.ecl.service.dto.OrderInfoDto;
import ru.clevertec.ecl.service.dto.UserDto;
import ru.clevertec.ecl.service.exception.NotFoundException;
import ru.clevertec.ecl.service.impl.OrderServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderServiceTest extends BaseIntegrationTest {

    @Autowired
    private OrderServiceImpl service;
    @Autowired
    private EntityManager manager;

    @Test
    void checkFindOrdersByUserIdShouldReturnNotEmpty(){
        Pageable pageable = PageRequest.of(0, 5, Direction.ASC, "id");
        Page<OrderDto> actual = service.findOrdersByUserId(1L, pageable);

        assertThat(actual).isNotEmpty();
    }

    @Test
    void checkFindOrdersByUserIdShouldReturnEmpty(){
        Pageable pageable = PageRequest.of(0, 5, Direction.ASC, "id");
        Page<OrderDto> actual = service.findOrdersByUserId(100L, pageable);

        assertThat(actual).isEmpty();
    }

    @Test
    void checkFindByIdShouldReturnNotNull() {
        assertThat(service.findById(1L)).isNotNull();
    }

    @Test
    void checkFindByIdShouldThrowNotFoundExc() {
        Assertions.assertThrows(NotFoundException.class, () -> service.findById(100L));
    }
    
    @Test
    void checkFindByIdByUserIdShouldReturnNotNull() {
        assertThat(service.findOrderByIdByUserId(1L, 1L)).isNotNull();
    }

    @Test
    void checkFindByIdByUserIdShouldThrowNotFoundExc() {
        Assertions.assertThrows(NotFoundException.class, () -> service.findOrderByIdByUserId(1L, 3L));
    }

    @Test
    void checkFindOrderCostTimeByIdByUserIdShouldReturnNotNull() {
        assertThat(service.findOrderCostTimeByIdByUserId(1L, 1L)).isNotNull();
    }

    @Test
    void checkFindOrderCostTimeByIdByUserIdShouldThrowNotFoundExc() {
        Assertions.assertThrows(NotFoundException.class, () -> service.findOrderCostTimeByIdByUserId(1L, 3L));
    }

    @Test
    void checkUpdateShouldReturnEquals() {
        OrderDto dto = new OrderDto();
        dto.setId(1L);
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        dto.setUserDto(userDto);
        dto.setStatusDto(StatusDto.DELIVERED);

        OrderDto actual = service.update(dto);

        assertThat(actual.getStatusDto()).isEqualTo(StatusDto.DELIVERED);
    }

    @Test
    void checkFindAllShouldReturnSize3() {
        int expectedSize = 3;
        Pageable pageable = PageRequest.of(0, 3, Direction.ASC, "id");
        Page<OrderDto> actual = service.findAll(pageable);
        assertThat(actual).hasSize(expectedSize);
    }

    @Test
    void checkCreateShouldReturnNotNullId() {
        OrderDto dto = new OrderDto();
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        dto.setUserDto(userDto);
        OrderInfoDto infoDto = new OrderInfoDto();
        GiftCertificateDto certificateDto = new GiftCertificateDto();
        certificateDto.setId(1L);
        infoDto.setGiftCertificateDto(certificateDto);
        infoDto.setCertificateQuantity(1);
        dto.setDetailsDto(List.of(infoDto));

        OrderDto actual = service.create(dto);
        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void checkCreateShouldThrowNotFoundExc() {
        OrderDto dto = new OrderDto();
        UserDto userDto = new UserDto();
        userDto.setId(100L);
        dto.setUserDto(userDto);
        OrderInfoDto infoDto = new OrderInfoDto();
        GiftCertificateDto certificateDto = new GiftCertificateDto();
        certificateDto.setId(1L);
        infoDto.setGiftCertificateDto(certificateDto);
        infoDto.setCertificateQuantity(1);
        dto.setDetailsDto(List.of(infoDto));

        Assertions.assertThrows(NotFoundException.class, () -> service.create(dto));
    }

    @Test
    void checkDeleteShouldSuccess() {
        service.delete(3L);
        manager.flush();
    }

    @Test
    void checkDeleteShouldThrowNotFoundExc() {
        Assertions.assertThrows(NotFoundException.class, () -> service.delete(100L));
    }
}
