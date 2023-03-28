package ru.clevertec.ecl.data.repository.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.data.repository.dao.GiftCertificateDao;
import ru.clevertec.ecl.service.exception.ClevertecException;
import ru.clevertec.ecl.service.exception.ClientException;
import ru.clevertec.ecl.service.exception.NotFoundException;

@Component
@RequiredArgsConstructor
public class GiftCertificateDaoImpl implements GiftCertificateDao {
    private static final String INSERT = "INSERT into gift_certificate (\"name\", description, price, duration) " +
            "VALUES (:name, :description, :price, :duration)";
    private static final String FIND_BY_ID = "SELECT g.id, g.\"name\", g.description, g.price, g.duration, g.create_date, g.last_update_date " +
            "FROM gift_certificate g WHERE g.id =:id";
    private static final String UPDATE = "UPDATE gift_certificate SET \"name\" = :name, description = :description, price = :price, " +
            "duration = :duration WHERE id = :id";
    private static final String DELETE = "DELETE FROM gift_certificate WHERE id = :id";
    private static final String DELETE_CERT_TAG_BY_CERT_ID = "DELETE FROM certificate_tag ct WHERE ct.certificate_id = :id";
    private static final String CREATE_CERT_TAG_ENTRY = "INSERT INTO certificate_tag (certificate_id, tag_id) VALUES (:cert_id, :tag_id)";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_PRICE = "price";
    private static final String COL_DURATION = "duration";
    private static final String COL_CREATED_DATE = "create_date";
    private static final String COL_LAST_UPDATE_DATE = "last_update_date";
    private static final String PARAM_CERT_ID = "cert_id";
    private static final String PARAM_TAG_ID = "tag_id";
    private static final String COL_TAG_ID = "t_id";
    private static final String COL_TAG_NAME = "t_name";
    private static final String PARAM_ID = "id";
    private static final String EXC_MSG_CREATE = "couldn't create new gift certificate";
    private static final String EXC_MSG_CREATE_CERT_TAG = "couldn't create new certificate-tag entry";
    private static final String EXC_MSG_NOT_FOUND_ID = "wasn't found certificate with id = ";
    private static final String EXC_MSG_UPDATE = "couldn't update certificate with id = ";
    private static final String EXC_MSG_DELETE = "couldn't delete certificate with id = ";
    public static final String EXC_MSG_CREATE_CERT_TAG_EXISTS = "a certificate with this tag already exists";
    public static final String EXC_MSG_CREATE_DESCR_CERT_EXISTS = "A certificate with this description already exists";
    public static final String CODE_CLIENT_CERT_TAG_CREATE = "40031";
    public static final String CODE_SERVER_ERROR_CERT_TAG_CREATE = "50031";
    public static final String CODE_CLIENT_CERT_UPD = "40013";
    public static final String CODE_CLIENT_CER_CREATE = "40011";
    public static final String CODE_SERVER_CERT_CREATE = "50011";
    public static final String CODE_CLIENT_CERT_READ = "40412";
    public static final String CODE_CLIENT_CERT_DEL = "40014";
    public static final String CODE_SERVER_CERT_TAG_DEL = "50034";

    private final NamedParameterJdbcTemplate template;

    @Override
    public void createCertificateTagEntry(Long certificateId, Long tagId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PARAM_CERT_ID, certificateId)
                .addValue(PARAM_TAG_ID, tagId);
        int rowUpd;
        try {
            rowUpd = template.update(CREATE_CERT_TAG_ENTRY, params);
        } catch (DataIntegrityViolationException | BadSqlGrammarException e) {
            throw new ClientException(EXC_MSG_CREATE_CERT_TAG_EXISTS, CODE_CLIENT_CERT_TAG_CREATE);
        }
        if (rowUpd == 0) {
            throw new ClevertecException(EXC_MSG_CREATE_CERT_TAG, CODE_SERVER_ERROR_CERT_TAG_CREATE);
        }
    }

    @Override
    public GiftCertificate updateByParams(String query, Long id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PARAM_ID, id);
        int rowUpdate;
        try {
            rowUpdate = template.update(query, params);
        } catch (DataIntegrityViolationException | BadSqlGrammarException e) {
            throw new ClientException(EXC_MSG_CREATE_CERT_TAG_EXISTS, CODE_CLIENT_CERT_UPD);
        }
        if (rowUpdate == 0) {
            throw new ClevertecException(EXC_MSG_UPDATE + id, CODE_CLIENT_CERT_UPD);
        }
        return findById(id);
    }

    @Override
    public List<GiftCertificate> findByRichParams(String query) {
        return template.query(query, rs -> {
            List<GiftCertificate> extracted = new ArrayList<>();
            while (rs.next()) {
                GiftCertificate certificate = new GiftCertificate();
                certificate.setId(rs.getLong(COL_ID));
                certificate.setName(rs.getString(COL_NAME));
                certificate.setDescription(rs.getString(COL_DESCRIPTION));
                certificate.setPrice(rs.getBigDecimal(COL_PRICE));
                certificate.setDuration(rs.getInt(COL_DURATION));
                certificate.setCreatedDate(rs.getTime(COL_CREATED_DATE));
                certificate.setLastUpdateDate(rs.getTime(COL_LAST_UPDATE_DATE));
                Tag tag = new Tag();
                tag.setId(rs.getLong(COL_TAG_ID));
                tag.setName(rs.getString(COL_TAG_NAME));
                List<Tag> tags = new ArrayList<>();
                tags.add(tag);
                certificate.setTags(tags);
                extracted.add(certificate);
            }
            return extracted;
        });
    }

    @Override
    public List<GiftCertificate> find(String query) {
        return template.query(query, this::mapRowCertificate);
    }

    @Override
    public GiftCertificate createByParams(String query) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            template.update(query, new MapSqlParameterSource(), keyHolder, new String[]{COL_ID});
        } catch (DuplicateKeyException e) {
            throw new ClientException(EXC_MSG_CREATE_DESCR_CERT_EXISTS, e, CODE_CLIENT_CER_CREATE);
        }
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new ClevertecException(EXC_MSG_CREATE, CODE_SERVER_CERT_CREATE);
        }
        Long id = key.longValue();
        return findById(id);
    }


    @Override
    public GiftCertificate findById(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_ID, id);
        try {
            return template.queryForObject(FIND_BY_ID, params, this::mapRowCertificate);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException(EXC_MSG_NOT_FOUND_ID + id, e, CODE_CLIENT_CERT_READ);
        }
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
            throw new ClientException(EXC_MSG_UPDATE + entity.getId(), CODE_CLIENT_CERT_UPD);
        }
        return findById(entity.getId());
    }

    @Override
    public void delete(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_ID, id);
        if (template.update(DELETE, params) != 1) {
            throw new ClientException(EXC_MSG_DELETE + id, CODE_CLIENT_CERT_DEL);
        }
    }

    @Override
    public void deleteCertificateTagByCertificateId(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_ID, id);
        try {
            template.update(DELETE_CERT_TAG_BY_CERT_ID, params);
        } catch (DataAccessException e) {
            throw new ClevertecException(EXC_MSG_DELETE + id, CODE_SERVER_CERT_TAG_DEL);
        }
    }

    private GiftCertificate mapRowCertificate(ResultSet resultSet, int rowNum) throws SQLException {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(resultSet.getLong(COL_ID));
        certificate.setName(resultSet.getString(COL_NAME));
        certificate.setPrice(resultSet.getBigDecimal(COL_PRICE));
        certificate.setDescription(resultSet.getString(COL_DESCRIPTION));
        certificate.setDuration(resultSet.getInt(COL_DURATION));
        certificate.setCreatedDate(resultSet.getTime(COL_CREATED_DATE));
        certificate.setLastUpdateDate(resultSet.getTime(COL_LAST_UPDATE_DATE));
        return certificate;
    }

    @Override
    public GiftCertificate create(GiftCertificate entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(COL_NAME, entity.getName())
                .addValue(COL_DESCRIPTION, entity.getDescription())
                .addValue(COL_PRICE, entity.getPrice())
                .addValue(COL_DURATION, entity.getDuration());
        try {
            template.update(INSERT, params, keyHolder, new String[]{COL_ID});
        } catch (DataIntegrityViolationException e) {
            throw new ClientException(EXC_MSG_CREATE, CODE_CLIENT_CER_CREATE);
        }
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new ClevertecException(EXC_MSG_CREATE, CODE_SERVER_CERT_CREATE);
        }
        Long id = key.longValue();
        return findById(id);
    }
}
