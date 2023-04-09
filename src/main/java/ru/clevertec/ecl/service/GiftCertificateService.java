package ru.clevertec.ecl.service;

import java.util.List;
import ru.clevertec.ecl.service.dto.GiftCertificateDto;
import ru.clevertec.ecl.service.dto.QueryParamsDto;

public interface GiftCertificateService extends CrudService<GiftCertificateDto, Long> {
    /**
     * gets a list of objects by {@link ru.clevertec.ecl.service.dto.QueryParamsDto} or all objects if there are no parameters. The default is sorted by ID. The size of the list is
     * determined by the parameters. The default list size and maximum size is defined
     * in @{@link ru.clevertec.ecl.service.util.PagingUtil}
     *
     * @param paramsDto parameters
     * @return serialized list of objects
     */
    List<GiftCertificateDto> findByParams(QueryParamsDto paramsDto);

    /**
     * updates an object by {@link ru.clevertec.ecl.service.dto.QueryParamsDto}
     *
     * @param paramsDto parameters
     * @param id        object identifier
     * @return updated object
     */
    GiftCertificateDto updateByParams(QueryParamsDto paramsDto, Long id);

    /**
     * creates an object based on the received parameters
     *
     * @param paramsDto parameters
     * @return serialized object
     */
    GiftCertificateDto createByParams(QueryParamsDto paramsDto);

}
