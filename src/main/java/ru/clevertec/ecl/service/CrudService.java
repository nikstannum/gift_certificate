package ru.clevertec.ecl.service;

import java.util.List;
import ru.clevertec.ecl.service.dto.QueryParamsDto;

public interface CrudService<T, K> {
    T create(QueryParamsDto paramsDto);

    T findById(K id);

    List<T> find(QueryParamsDto paramsDto);

    T update(QueryParamsDto paramsDto, Long id);

    void delete(K id);
}
