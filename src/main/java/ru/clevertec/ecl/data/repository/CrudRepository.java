package ru.clevertec.ecl.data.repository;

import java.util.List;
import ru.clevertec.ecl.data.entity.QueryParams;

public interface CrudRepository<T, K> {
    T create(T entity);

    /**
     * serializes an object from the database
     *
     * @param id the object id
     * @return this object
     */
    T findById(K id);

    /**
     * serializes all objects in the database
     *
     * @return serialized list of objects
     */
    List<T> find(QueryParams queryParams);

    /**
     * updates an object in the database
     *
     * @param entity the object itself
     * @return this updated object
     */
    T update(T entity);

    /**
     * removes an object from the database
     *
     * @param id object id
     */
    void delete(K id);

}
