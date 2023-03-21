package ru.clevertec.ecl.data.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.data.repository.GiftCertificateRepository;
import ru.clevertec.ecl.service.exception.ClevertecException;
import ru.clevertec.ecl.service.exception.ClientException;
import ru.clevertec.ecl.service.exception.NotFoundException;

@Repository
@RequiredArgsConstructor
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {
    private static final String INSERT = "INSERT into gift_certificate (\"name\", description, price, duration) " +
            "VALUES (:name, :description, :price, :duration)";
    private static final String FIND_BY_ID = "SELECT g.id, g.\"name\", g.description, g.price, g.duration, g.create_date, g.last_update_tag " +
            "FROM gift_certificate g WHERE g.id =:id";
    private static final String FIND_BY_TAG = "SELECT g.id, g.\"name\", g.description, g.price, g.duration, g.create_date, g.last_update_tag " +
            "t.\"name\" FROM gift_certificate g JOIN certificate_tag ct ON g.id = ct.certificate_id JOIN tag t ON ct.tag_id = t.id " +
            "WHERE t.\"name\" LIKE :name";

    private static final String FIND_BY_NAME = "SELECT g.id, g.\"name\", g.description, g.price, g.duration, g.create_date, g.last_update_tag " +
            "FROM gift_certificate g JOIN certificate_tag ct ON g.id = ct.certificate_id JOIN tag t ON ct.tag_id = t.id " +
            "WHERE g.\"name\"  LIKE :name";

    private static final String FIND_BY_DESCRIPTION = "SELECT g.id, g.\"name\", g.description, g.price, g.duration, g.create_date, " +
            "g.last_update_tag FROM gift_certificate g JOIN certificate_tag ct ON g.id = ct.certificate_id JOIN tag t ON ct.tag_id = t.id " +
            "WHERE g.description LIKE :name";


    private static final String FIND_ALL = "SELECT g.id, g.\"name\", g.description, g.price, g.duration FROM gift_certificate g";
    private static final String UPDATE = "UPDATE gift_certificate SET \"name\" = :name, description = :description, price = :price, " +
            "duration = :duration WHERE id = :id";
    private static final String DELETE = "DELETE FROM gift_certificate WHERE id = :id";

    private static final String COL_ID = "id";

    private static final String COL_NAME = "name";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_PRICE = "price";
    private static final String COL_DURATION = "duration";
    private static final String COL_CREATED_DATE = "create_date";
    private static final String COL_LAST_UPDATE_DATE = "last_update_date";
    private static final String PARAM_ID = "id";
    private static final String PARAM_NAME = "name";
    private static final String EXC_MSG_CREATE = "couldn't create new gift certificate";
    private static final String EXC_MSG_NOT_FOUND_ID = "wasn't found certificate with id = ";
    private static final String EXC_MSG_NOT_FOUND_TAG = "wasn't found certificate with tag: ";
    private static final String EXC_MSG_UPDATE = "couldn't update certificate with id = ";
    private static final String EXC_MSG_NOT_FOUND_NAME = "wasn't found certificate by string: ";
    private static final String EXC_MSG_NOT_FOUND_DESCRIPTION = "wasn't found certificate by description string: ";


    private final NamedParameterJdbcTemplate template;

    @Override
    public GiftCertificate create(GiftCertificate entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(COL_NAME, entity.getName())
                .addValue(COL_DESCRIPTION, entity.getDescription())
                .addValue(COL_PRICE, entity.getPrice())
                .addValue(COL_DURATION, entity.getDuration());
        template.update(INSERT, params, keyHolder, new String[]{COL_ID});
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new ClevertecException(EXC_MSG_CREATE);
        }
        Long id = key.longValue();
        return findById(id);
    }

    @Override
    public GiftCertificate findById(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_ID, id);
        try {
            return template.queryForObject(FIND_BY_ID, params, this::mapRow);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException(EXC_MSG_NOT_FOUND_ID + id, e);
        }
    }

    @Override
    public List<GiftCertificate> findByTagName(Tag tag) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_NAME, tag.getName());
        try {
            return template.query(FIND_BY_TAG, params, this::mapRow);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException(EXC_MSG_NOT_FOUND_TAG + tag.getName(), e);
        }
    }

    @Override
    public List<GiftCertificate> findByName(String partName) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_NAME, "%" + partName + "%");
        try {
            return template.query(FIND_BY_NAME, params, this::mapRow);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException(EXC_MSG_NOT_FOUND_NAME + partName, e);
        }
    }

    @Override
    public List<GiftCertificate> findByDescription(String partDescription) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_NAME, "%" + partDescription + "%");
        try {
            return template.query(FIND_BY_DESCRIPTION, params, this::mapRow);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException(EXC_MSG_NOT_FOUND_DESCRIPTION + partDescription, e);
        }
    }

    @Override
    public List<GiftCertificate> findAll() {
        return template.query(FIND_ALL, this::mapRow);
    }

    @Override
    public GiftCertificate update(GiftCertificate entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(COL_NAME, entity.getName())
                .addValue(COL_DESCRIPTION, entity.getDescription())
                .addValue(COL_PRICE, entity.getPrice())
                .addValue(COL_DURATION, entity.getDuration())
                .addValue(PARAM_ID, entity.getId());
        int rowUpdated = template.update(UPDATE, params);
        if (rowUpdated == 0) {
            throw new ClientException(EXC_MSG_UPDATE + entity.getId());
        }
        return findById(entity.getId());
    }

    @Override
    public boolean delete(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_ID, id);
        return template.update(DELETE, params) == 1;
    }

    private GiftCertificate mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(resultSet.getLong(COL_ID));
        certificate.setName(resultSet.getString(COL_NAME));
        certificate.setPrice(resultSet.getBigDecimal(COL_PRICE));
        certificate.setDuration(resultSet.getInt(COL_DURATION));
        certificate.setCreatedDate(convert(resultSet.getDate(COL_CREATED_DATE)));
        certificate.setLastUpdateDate(convert(resultSet.getDate(COL_LAST_UPDATE_DATE)));
        return certificate;
    }

    private LocalDateTime convert(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
