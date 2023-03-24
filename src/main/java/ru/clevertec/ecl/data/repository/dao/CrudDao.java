package ru.clevertec.ecl.data.repository.dao;

import java.util.List;

public interface CrudDao<T, K> {
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
     * serializes all objects in the database
     *
     * @return serialized list of objects
     */
    List<T> find(String query);

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
     * @return result of deletion
     */
    void delete(K id);

}
