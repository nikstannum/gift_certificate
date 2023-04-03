package ru.clevertec.ecl.data.repository;

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
