package ru.clevertec.ecl.data.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.clevertec.ecl.data.entity.Tag;

public interface TagRepository extends CrudRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {
    /**
     * method returning a tag inside container by name
     *
     * @param name name of tag
     * @return a container object
     */
    Optional<Tag> findByName(String name);

    /**
     * method to get the most widely used tag with the highest order value for this user
     *
     * @param useId user ID
     * @return the most popular {@link ru.clevertec.ecl.data.entity.Tag} for this user
     */
    @Query(value = """
            SELECT t.id, t.name, count(t.id) FROM tag t
            JOIN certificate_tag ct ON t.id = ct.tag_id
            JOIN gift_certificate gc ON ct.certificate_id = gc.id
            JOIN order_infos oi ON oi.certificate_id = gc.id
            JOIN orders o ON oi.order_id = o.order_id
            WHERE o.user_id = :id
            GROUP BY t.id, o.total_cost
            ORDER BY count(t.id) DESC, o.total_cost DESC
            LIMIT 1
            """, nativeQuery = true)
    Tag findMostPopularTag(@Param("id") Long useId);
}
