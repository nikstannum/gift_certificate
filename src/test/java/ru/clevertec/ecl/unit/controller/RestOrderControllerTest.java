package ru.clevertec.ecl.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.ecl.service.dto.GiftCertificateDto;
import ru.clevertec.ecl.service.dto.OrderCreateDto;
import ru.clevertec.ecl.service.dto.OrderCreateDto.Item;
import ru.clevertec.ecl.service.dto.OrderDto;
import ru.clevertec.ecl.service.dto.OrderDto.StatusDto;
import ru.clevertec.ecl.service.dto.OrderInfoDto;
import ru.clevertec.ecl.service.dto.TagDto;
import ru.clevertec.ecl.service.dto.UserDto;
import ru.clevertec.ecl.service.impl.OrderServiceImpl;
import ru.clevertec.ecl.web.controller.RestOrderController;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestOrderController.class)
public class RestOrderControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private OrderServiceImpl service;

    @Test
    void checkCreateOrderShouldReturnOrderAndStatus201() throws Exception {
        OrderCreateDto createDto = prepareOrderCreateDto();
        OrderDto returned = prepareOrderDto();
        Mockito.doReturn(returned).when(service).create(Mockito.any());

        mvc.perform(post("/api/orders")
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.detailsDto[0].giftCertificateDto.tags[*].name", containsInAnyOrder("tag 1", "tag 2")));
    }

    @Test
    void checkFindAllShouldReturnStatus200() throws Exception {
        OrderDto orderDto1 = getOrderDro(1L);
        OrderDto orderDto2 = getOrderDro(2L);
        OrderDto orderDto3 = getOrderDro(3L);
        OrderDto orderDto4 = getOrderDro(4L);
        OrderDto orderDto5 = getOrderDro(5L);
        Page<OrderDto> page = new PageImpl<>(List.of(orderDto1, orderDto2, orderDto3, orderDto4, orderDto5));
        Mockito.doReturn(page).when(service).findAll(Mockito.any());
        mvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.content[*].id", containsInAnyOrder(1, 2, 3, 4, 5)));
    }

    @Test
    void checkFindByIdShouldReturnOrderAndStatus200() throws Exception {
        OrderDto orderDto = prepareOrderDto();
        Mockito.doReturn(orderDto).when(service).findById(1L);
        mvc.perform(get("/api/orders/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.detailsDto[0].giftCertificateDto.tags[*].name", containsInAnyOrder("tag 1", "tag 2")));
    }

    @Test
    void checkDeleteShouldReturnStatus204() throws Exception {
        mvc.perform(delete("/api/orders/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    private OrderDto getOrderDro(Long id) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(id);
        return orderDto;
    }

    private OrderDto prepareOrderDto() {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(1L);
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        orderDto.setUserDto(userDto);
        orderDto.setStatusDto(StatusDto.PENDING);
        OrderInfoDto infoDto = new OrderInfoDto();
        infoDto.setId(1L);
        infoDto.setOrderDto(orderDto);
        infoDto.setCreateDate(Instant.now());
        infoDto.setCertificatePrice(BigDecimal.valueOf(12.09));
        infoDto.setCertificateQuantity(2);
        GiftCertificateDto certificateDto = new GiftCertificateDto();
        certificateDto.setId(1L);
        certificateDto.setName("test name");
        certificateDto.setDuration(1);
        certificateDto.setPrice(BigDecimal.valueOf(12.09));
        certificateDto.setDescription("test description");
        certificateDto.setCreateDate(Instant.now());
        certificateDto.setLastUpdateDate(Instant.now());
        TagDto tagDto1 = new TagDto();
        tagDto1.setId(1L);
        tagDto1.setName("tag 1");
        TagDto tagDto2 = new TagDto();
        tagDto2.setId(2L);
        tagDto2.setName("tag 2");
        certificateDto.setTags(List.of(tagDto1, tagDto2));
        infoDto.setGiftCertificateDto(certificateDto);
        orderDto.setDetailsDto(List.of(infoDto));
        return orderDto;
    }

    private OrderCreateDto prepareOrderCreateDto() {
        OrderCreateDto createDto = new OrderCreateDto();
        createDto.setUserId(1L);
        Item item = new Item();
        item.setCertId(1L);
        item.setQuantity(2);
        createDto.setItems(List.of(item));
        return createDto;
    }
}
