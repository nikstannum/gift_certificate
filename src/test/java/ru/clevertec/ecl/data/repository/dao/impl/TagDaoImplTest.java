package ru.clevertec.ecl.data.repository.dao.impl;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.clevertec.ecl.ContextConfig;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.data.repository.dao.TagDao;
import ru.clevertec.ecl.service.exception.NotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(ContextConfig.class)
class TagDaoImplTest {

    private TagDao dao;

    @BeforeEach
    void setUp() {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:sql/schema.sql")
                .addScript("classpath:sql/data.sql")
                .build();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        dao = new TagDaoImpl(template);
    }

    @Nested
    class FindTagByNameTest {

        @Test
        void checkFindByTagNameShouldEmpty() {
            String tagName = "qwerty";
            Optional<Tag> tagOpt = dao.findTagByName(tagName);
            assertThat(tagOpt).isEmpty();
        }

        @Test
        void checkFindByTagNameShouldPresent() {
            String tagName = "male";
            Optional<Tag> tagOpt = dao.findTagByName(tagName);
            assertThat(tagOpt).isPresent();
        }

        @Test
        void checkFindTagByNameShouldReturnEquals() {
            String tagName = "male";

            Optional<Tag> tagOpt = dao.findTagByName(tagName);
            Tag tag = tagOpt.orElseThrow();

            assertThat(tag.getName()).isEqualTo(tagName);
        }
    }

    @Nested
    class FindTagsByCertificateIdTest {

        @Test
        void checkFindTagsByGiftCertificateIdShouldReturnSize1() {
            List<Tag> list = dao.findTagsByGiftCertificateId(2L);
            assertThat(list).hasSize(1);
        }

        @Test
        void checkFindTagsByGiftCertificateIdShouldReturnSize2() {
            List<Tag> list = dao.findTagsByGiftCertificateId(1L);
            assertThat(list).hasSize(2);
        }

        @ParameterizedTest
        @ValueSource(longs = {-1, 0, 200})
        void checkFindTagsByGiftCertificateIdShouldEmpty(long id) {
            List<Tag> list = dao.findTagsByGiftCertificateId(id);
            assertThat(list).hasSize(0);
        }
    }

    @Test
    void create() {
        Tag expected = new Tag();
        expected.setName("test");

        Tag actual = dao.create(expected);

        assertThat(actual.getName()).isEqualTo(expected.getName());
    }

    @Nested
    class FindByIdTest {

        @ParameterizedTest
        @ValueSource(longs = {-1, 0, 200})
        void checkFindByIdShouldThrowNotFoundExc(long id) {
            Assertions.assertThrows(NotFoundException.class, () -> dao.findById(id));
        }

        @Test
        void checkFindByIdShouldReturn1() {
            Tag expectedTag = new Tag();
            expectedTag.setId(1L);
            expectedTag.setName("male");

            Tag actualTag = dao.findById(1L);

            assertThat(actualTag).isEqualTo(expectedTag);
        }
    }

    @Test
    void checkFindShouldReturnSize7() {
        assertThat(dao.find("")).hasSize(7);
    }

    @Test
    void update() {
        Tag expected = dao.findById(1L);
        expected.setName("update");

        Tag actual = dao.update(expected);

        assertThat(actual).isEqualTo(expected);
    }
}
