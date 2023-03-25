package ru.clevertec.ecl.data.repository.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.clevertec.ecl.ContextConfig;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.data.repository.GiftCertificateRepository;
import ru.clevertec.ecl.data.repository.dao.GiftCertificateDao;
import ru.clevertec.ecl.data.repository.dao.TagDao;
import ru.clevertec.ecl.data.repository.dao.impl.GiftCertificateDaoImpl;
import ru.clevertec.ecl.data.repository.dao.impl.TagDaoImpl;
import ru.clevertec.ecl.data.repository.util.QueryBuilder;
import ru.clevertec.ecl.service.exception.ClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig(ContextConfig.class)
class GiftCertificateRepositoryImplTest {

    private final QueryBuilder queryBuilder;
    private GiftCertificateRepository repository;

    @Autowired
    public GiftCertificateRepositoryImplTest(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    @BeforeEach
    void setUp() {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:sql/schema.sql")
                .addScript("classpath:sql/data.sql")
                .build();
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        TagDao tagDao = new TagDaoImpl(template);
        GiftCertificateDao giftDao = new GiftCertificateDaoImpl(template);
        repository = new GiftCertificateRepositoryImpl(giftDao, tagDao, queryBuilder);
    }

    @Nested
    class FindTagByNameTest {

        @Test
        void checkFindTagByNameShouldPresent() {
            Optional<Tag> actual = repository.findTagByName("male");
            assertThat(actual).isPresent();
        }

        @Test
        void checkFindTagByNameShouldEmpty() {
            Optional<Tag> actual = repository.findTagByName("qwerty");
            assertThat(actual).isEmpty();
        }

        @Test
        void checkFindTagByNameShouldEquals() {
            Tag expected = new Tag();
            expected.setId(1L);
            expected.setName("male");

            Optional<Tag> actualOpt = repository.findTagByName("male");
            Tag actual = actualOpt.orElseThrow();

            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class CreateCertificateTagEntryTest {

        @Test
        void checkCreateCertificateTagEntryShouldSuccess() {
            repository = Mockito.mock(GiftCertificateRepository.class);
            repository.createCertificateTagEntry(1L, 5L);
            Mockito.verify(repository).createCertificateTagEntry(1L, 5L);
        }

        private static Stream<Arguments> provideCertIdTagId() {
            return Stream.of(Arguments.of(-1L, 5L),
                    Arguments.of(-1L, -1L),
                    Arguments.of(1L, 1L),
                    Arguments.of(5L, -1L),
                    Arguments.of(0L, 0L)
            );
        }

        @ParameterizedTest
        @MethodSource("provideCertIdTagId")
        void checkCreateCertificateTagEntryShouldThrowClientExc(Long certId, Long tagId) {
            assertThrows(ClientException.class, () -> repository.createCertificateTagEntry(certId, tagId));
        }
    }

    @Nested
    class FindTagsByCertificateIdTest {

        @Test
        void checkFindTagsByGiftCertificateIdShouldReturnSize1() {
            List<Tag> list = repository.findTagsByCertificateId(2L);
            assertThat(list).hasSize(1);
        }

        @Test
        void checkFindTagsByGiftCertificateIdShouldReturnSize2() {
            List<Tag> list = repository.findTagsByCertificateId(1L);
            assertThat(list).hasSize(2);
        }

        @ParameterizedTest
        @ValueSource(longs = {-1, 0, 200})
        void checkFindTagsByGiftCertificateIdShouldEmpty(long id) {
            List<Tag> list = repository.findTagsByCertificateId(id);
            assertThat(list).hasSize(0);
        }
    }

    @Nested
    class UpdateByParamsTest {

        private static Stream<Arguments> provideQueryParamsCertId() {
            Long certId = 1L;
            List<Object> name = getChangedParamName();
            List<Object> descr = getChangedParamDescr();
            List<Object> price = getChangedParamPrice();
            List<Object> duration = getChangedParamDuration();
            List<Object> namePrice = getChangedParamsNamePrice();
            List<Object> descrDuration = getChangedParamsDescrDuration();
            List<Object> allCertParams = getChangedAllCertParams();
            List<Object> paramTag = getChangedParamTag();
            List<Object> paramsTags = getChangedParamsTags();
            List<Object> all = getChangedAllParamsTwoTags();

            return Stream.of(Arguments.of(name, certId),
                    Arguments.of(descr, certId),
                    Arguments.of(price, certId),
                    Arguments.of(duration, certId),
                    Arguments.of(namePrice, certId),
                    Arguments.of(descrDuration, certId),
                    Arguments.of(allCertParams, certId),
                    Arguments.of(paramTag, certId),
                    Arguments.of(duration, certId),
                    Arguments.of(paramsTags, certId),
                    Arguments.of(all, certId)
            );
        }

        private static List<Object> getChangedParamName() {
            GiftCertificate expected = new GiftCertificate();
            expected.setId(1L);
            expected.setName("test update name");
            expected.setDescription("parachute jump from an airplane from a height of 2000 meters with an instructor");
            expected.setPrice(BigDecimal.valueOf(199.99));
            expected.setDuration(7);

            QueryParams queryParams = new QueryParams();
            queryParams.setCert("name:test update name");

            List<Object> list = new ArrayList<>();
            list.add(queryParams);
            list.add(expected);
            return list;
        }

        private static List<Object> getChangedParamDescr() {
            GiftCertificate expected = new GiftCertificate();
            expected.setId(1L);
            expected.setName("skydiving");
            expected.setDescription("test update description");
            expected.setPrice(BigDecimal.valueOf(199.99));
            expected.setDuration(7);

            QueryParams queryParams = new QueryParams();
            queryParams.setCert("descr:test update description");

            List<Object> list = new ArrayList<>();
            list.add(queryParams);
            list.add(expected);
            return list;
        }

        private static List<Object> getChangedParamPrice() {
            GiftCertificate expected = new GiftCertificate();
            expected.setId(1L);
            expected.setName("skydiving");
            expected.setDescription("parachute jump from an airplane from a height of 2000 meters with an instructor");
            expected.setPrice(BigDecimal.valueOf(99.99));
            expected.setDuration(7);

            QueryParams queryParams = new QueryParams();
            queryParams.setCert("price:99.99");

            List<Object> list = new ArrayList<>();
            list.add(queryParams);
            list.add(expected);
            return list;
        }

        private static  List<Object> getChangedParamDuration() {
            GiftCertificate expected = new GiftCertificate();
            expected.setId(1L);
            expected.setName("skydiving");
            expected.setDescription("parachute jump from an airplane from a height of 2000 meters with an instructor");
            expected.setPrice(BigDecimal.valueOf(199.99));
            expected.setDuration(9999);

            QueryParams queryParams = new QueryParams();
            queryParams.setCert("duration:9999");

            List<Object> list = new ArrayList<>();
            list.add(queryParams);
            list.add(expected);
            return list;
        }

        private static List<Object> getChangedParamsNamePrice() {
            GiftCertificate expected = new GiftCertificate();
            expected.setId(1L);
            expected.setName("test update name");
            expected.setDescription("parachute jump from an airplane from a height of 2000 meters with an instructor");
            expected.setPrice(BigDecimal.valueOf(99.99));
            expected.setDuration(7);

            QueryParams queryParams = new QueryParams();
            queryParams.setCert("name:test update name,price:99.99");

            List<Object> list = new ArrayList<>();
            list.add(queryParams);
            list.add(expected);
            return list;
        }

        private static List<Object> getChangedParamsDescrDuration() {
            GiftCertificate expected = new GiftCertificate();
            expected.setId(1L);
            expected.setName("skydiving");
            expected.setDescription("test update description");
            expected.setPrice(BigDecimal.valueOf(199.99));
            expected.setDuration(9999);

            QueryParams queryParams = new QueryParams();
            queryParams.setCert("descr:test update description,duration:9999");

            List<Object> list = new ArrayList<>();
            list.add(queryParams);
            list.add(expected);
            return list;
        }

        private static List<Object> getChangedAllCertParams() {
            GiftCertificate expected = new GiftCertificate();
            expected.setId(1L);
            expected.setName("test update name");
            expected.setDescription("test update description");
            expected.setPrice(BigDecimal.valueOf(99.99));
            expected.setDuration(9999);

            QueryParams queryParams = new QueryParams();
            queryParams.setCert("name:test update name,descr:test update description,duration:9999,price:99.99");

            List<Object> list = new ArrayList<>();
            list.add(queryParams);
            list.add(expected);
            return list;
        }

        private static List<Object> getChangedParamTag() {
            GiftCertificate expected = new GiftCertificate();
            expected.setId(1L);
            expected.setName("skydiving");
            expected.setDescription("parachute jump from an airplane from a height of 2000 meters with an instructor");
            expected.setPrice(BigDecimal.valueOf(199.99));
            expected.setDuration(7);

            QueryParams queryParams = new QueryParams();
            queryParams.setTag("name:new tag");

            List<Object> list = new ArrayList<>();
            list.add(queryParams);
            list.add(expected);
            return list;
        }

        private static List<Object> getChangedParamsTags() {
            GiftCertificate expected = new GiftCertificate();
            expected.setId(1L);
            expected.setName("skydiving");
            expected.setDescription("parachute jump from an airplane from a height of 2000 meters with an instructor");
            expected.setPrice(BigDecimal.valueOf(199.99));
            expected.setDuration(7);

            QueryParams queryParams = new QueryParams();
            queryParams.setTag("name:new tag,name:new tag 2");

            List<Object> list = new ArrayList<>();
            list.add(queryParams);
            list.add(expected);
            return list;
        }

        private static List<Object> getChangedAllParamsTwoTags() {
            GiftCertificate expected = new GiftCertificate();
            expected.setId(1L);
            expected.setName("test update name");
            expected.setDescription("test update description");
            expected.setPrice(BigDecimal.valueOf(99.99));
            expected.setDuration(9999);

            QueryParams queryParams = new QueryParams();
            queryParams.setCert("name:test update name,descr:test update description,duration:9999,price:99.99");
            queryParams.setTag("name:new tag,name:new tag 2");

            List<Object> list = new ArrayList<>();
            list.add(queryParams);
            list.add(expected);
            return list;
        }

        @ParameterizedTest
        @MethodSource("provideQueryParamsCertId")
        void updateByParams(List<Object> list, Long id) {
            QueryParams params = (QueryParams) list.get(0);
            GiftCertificate expected = (GiftCertificate) list.get(1);

            GiftCertificate actual = repository.updateByParams(params, id);
            actual.setCreatedDate(null);
            actual.setLastUpdateDate(null);

            assertThat(actual).isEqualTo(expected);
        }
    }

    @Test
    void createTag() {
    }

    @Test
    void find() {
    }

    @Test
    void create() {
    }

    @Test
    void createByParams() {
    }

    @Test
    void findById() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}