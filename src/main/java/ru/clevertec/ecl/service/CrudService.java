package ru.clevertec.ecl.service;

import java.util.List;
import ru.clevertec.ecl.service.dto.QueryParamsDto;

public interface CrudService<T, K> {
    /**
     * creates an object based on the received parameters
     *
     * @param paramsDto parameters
     * @return serialized object
     */
    T create(QueryParamsDto paramsDto);

    /**
     * get object by id
     *
     * @param id object identifier
     * @return desired object
     */
    T findById(K id);

    /**
     * gets a list of objects by parameters or all objects if there are no parameters. The default is sorted by ID. The size of the list is
     * determined by the parameters. The default list size and maximum size is defined in @{@link ru.clevertec.ecl.data.repository.util.QueryBuilder}
     *
     * @param paramsDto parameters
     * @return serialized list of objects
     */
    List<T> find(QueryParamsDto paramsDto);

    /**
     * updates an object
     *
     * @param paramsDto parameters
     * @param id        object identifier
     * @return updated object
     */
    T update(QueryParamsDto paramsDto, Long id);

    /**
     * @param id object identifier
     */
    void delete(K id);
}
