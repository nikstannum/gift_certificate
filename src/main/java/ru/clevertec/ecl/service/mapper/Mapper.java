package ru.clevertec.ecl.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.Order;
import ru.clevertec.ecl.data.entity.OrderInfo;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.data.entity.User;
import ru.clevertec.ecl.data.entity.User.UserRole;
import ru.clevertec.ecl.service.dto.GiftCertificateDto;
import ru.clevertec.ecl.service.dto.OrderDto;
import ru.clevertec.ecl.service.dto.OrderInfoDto;
import ru.clevertec.ecl.service.dto.QueryParamsDto;
import ru.clevertec.ecl.service.dto.TagDto;
import ru.clevertec.ecl.service.dto.UserDto;
import ru.clevertec.ecl.service.dto.UserDto.UserRoleDto;

@Component
@RequiredArgsConstructor
public class Mapper {
    public static final String PASSWORD_ENCR = "****";
    private final TagMapper tagMapper;
    private final GiftCertificateMapper giftCertificateMapper;
    private final QueryMapper queryMapper;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final OrderInfoMapper orderInfoMapper;
    private final RoleMapper roleMapper;

    public UserRoleDto convert(UserRole userRole) {
        return roleMapper.toDto(userRole);
    }

    public UserRole convert(UserRoleDto dto) {
        return roleMapper.toModel(dto);
    }

    public OrderInfoDto convert(OrderInfo entity) {
        return orderInfoMapper.toDto(entity);
    }

    public OrderInfo convert(OrderInfoDto dto) {
        return orderInfoMapper.toModel(dto);
    }

    public User convert(UserDto dto) {
        UserRole role = convert(dto.getUserRoleDto());
        User user = userMapper.toModel(dto);
        user.setUserRole(role);
        return user;
    }

    public UserDto convert(User entity) {
        UserRoleDto roleDto = convert(entity.getUserRole());
        UserDto userDto = userMapper.toDto(entity);
        userDto.setUserRoleDto(roleDto);
        userDto.setPassword(PASSWORD_ENCR);
        return userDto;
    }

    public Order convert(OrderDto dto) {
        return orderMapper.toModel(dto);
    }

    public OrderDto convert(Order entity) {
        return orderMapper.toDto(entity);
    }

    public Tag convert(TagDto dto) {
        return tagMapper.toModel(dto);
    }

    public TagDto convert(Tag entity) {
        return tagMapper.toDto(entity);
    }

    public GiftCertificate convert(GiftCertificateDto dto) {
        return giftCertificateMapper.toModel(dto);
    }

    public GiftCertificateDto convert(GiftCertificate entity) {
        return giftCertificateMapper.toDto(entity);
    }

    public QueryParams convert(QueryParamsDto dto) {
        return queryMapper.toModel(dto);
    }

    public QueryParamsDto convert(QueryParams entity) {
        return queryMapper.toDto(entity);
    }

}
