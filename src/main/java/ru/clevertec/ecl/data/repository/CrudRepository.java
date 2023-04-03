package ru.clevertec.ecl.data.repository;

import java.util.List;

public interface CrudRepository<T, K> {

    /**
     * serializes an object to the database
     *
     * @param entity the serializable object
     * @return the same object from the database
     */
    T create(T entity);

    /**
     * serializes an object from the database
     *
     * @param id the object id
     * @return this object
     */
    T findById(K id);

    /**
     * serializes a list of objects
     *
     * @param limit  sample size
     * @param offset number of elements behind
     * @return serialized list of objects
     */
    List<T> findAll(int limit, long offset);

    /**
     * updates an object in the database
     *
     * @param entity the object itself
     * @return this updated object
     */
    T update(T entity);

    /**
     * @param id object id
     */
    void delete(K id);
}
