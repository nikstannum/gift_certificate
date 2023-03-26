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
     * serializes a list of objects by parameters, or all objects if there are no parameters. The default is sorted by ID. The size of the list is
     * determined by the parameters. The default list size and maximum size is defined in @{@link ru.clevertec.ecl.data.repository.util.QueryBuilder}
     *
     * @param query SQL query
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
     * @param id object id
     */
    void delete(K id);

}
