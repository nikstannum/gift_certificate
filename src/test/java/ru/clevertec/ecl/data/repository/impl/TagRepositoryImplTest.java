package ru.clevertec.ecl.data.repository.impl;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.clevertec.ecl.TestConfig;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.data.repository.TagRepository;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringJUnitConfig(TestConfig.class)
class TagRepositoryImplTest {

    public static final String TAG_EXTREME = "extreme";
    public static final String TAG_NOT_EXISTS = "qwerty";

    private final TagRepository repository;

    @Autowired
    public TagRepositoryImplTest(TagRepository repository) {
        this.repository = repository;
    }

    @Test
    void checkFindTagByNameShouldNotEmpty() {
        Optional<Tag> optionalTag = repository.findTagByName(TAG_EXTREME);

        assertThat(optionalTag).isNotEmpty();
    }

    @Test
    void checkFindTagByNameShouldEmpty() {
        Optional<Tag> optionalTag = repository.findTagByName(TAG_NOT_EXISTS);

        assertThat(optionalTag).isEmpty();
    }

    @Test
    void create() {
        Tag tag = new Tag();
        tag.setName(TAG_NOT_EXISTS);

        repository.create(tag);

        assertThat(tag.getId()).isNotNull();
    }

    @Test
    void findById() {
        Tag tag = repository.findById(3L);
        assertThat(tag.getName()).isEqualTo(TAG_EXTREME);
    }

    @Test
    void update() {
        Tag tag = new Tag();
        tag.setId(3L);
        tag.setName(TAG_NOT_EXISTS);

        repository.update(tag);

        assertThat(tag.getName()).isEqualTo(TAG_NOT_EXISTS);
    }
}
