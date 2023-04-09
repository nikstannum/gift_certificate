package ru.clevertec.ecl.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudService<T, K> {

    /**
     * get object by id
     *
     * @param id object identifier
     * @return desired object
     */
    T findById(K id);

    /**
     * updates an object
     *
     * @param dto object for updating
     * @return updated object
     */
    T update(T dto);

    /**
     * get paging objects
     *
     * @param pageable abstract interface for pagination information
     * @return page of objects
     */
    Page<T> findAll(Pageable pageable);

    /**
     * creates an object
     *
     * @param dto object for creation
     * @return created object
     */
    T create(T dto);

    /**
     * removes an object by its id
     *
     * @param id object identifier
     */
    void delete(K id);
}
