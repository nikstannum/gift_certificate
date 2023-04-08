package ru.clevertec.ecl.data.repository;

import java.util.Optional;
import ru.clevertec.ecl.data.entity.Tag;

public interface TagRepository extends CrudRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
}
