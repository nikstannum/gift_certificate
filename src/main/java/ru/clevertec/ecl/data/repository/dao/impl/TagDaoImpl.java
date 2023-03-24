package ru.clevertec.ecl.data.repository.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.data.repository.dao.TagDao;
import ru.clevertec.ecl.service.exception.ClevertecException;
import ru.clevertec.ecl.service.exception.ClientException;
import ru.clevertec.ecl.service.exception.NotFoundException;

@Component
@RequiredArgsConstructor
public class TagDaoImpl implements TagDao {
    private static final String FIND_BY_ID = "SELECT t.id, t.\"name\" FROM tag t WHERE t.id = :id";
    private static final String FIND_ALL = "SELECT t.id, t.\"name\" FROM tag t";
    private static final String FIND_TAGS_BY_CERTIFICATE_ID = "SELECT t.id, t.\"name\" FROM tag t " +
            "JOIN certificate_tag ct ON t.id = ct.tag_id WHERE ct.certificate_id = :id";
    private static final String FIND_BY_TAG_NAME = "SELECT t.id, t.\"name\" FROM tag t WHERE t.\"name\" = :tagName";
    private static final String INSERT = "INSERT INTO tag (\"name\") VALUES (:name)";
    private static final String UPDATE = "UPDATE tag SET \"name\" = :name WHERE id = :id";
    private static final String DELETE = "DELETE FROM tag WHERE id = :id";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String PARAM_ID = "id";
    private static final String PARAM_TAG_NAME = "tagName";
    private static final String EXC_MSG_CREATE = "couldn't create new tag";
    private static final String EXC_MSG_NOT_FOUND = "wasn't found tag with id = ";
    private static final String EXC_MSG_UPDATE = "couldn't update tag with id = ";
    private static final String EXC_MSG_DELETE = "couldn't delete tag with id = ";

    private final NamedParameterJdbcTemplate template;

    @Override
    public Optional<Tag> findTagByName(String tagName) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_TAG_NAME, tagName);
        try {
            return Optional.ofNullable(template.queryForObject(FIND_BY_TAG_NAME, params, this::mapRow));
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Tag> findTagsByGiftCertificateId(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_ID, id);
        return template.query(FIND_TAGS_BY_CERTIFICATE_ID, params, this::mapRow);
    }

    @Override
    public Tag create(Tag entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(COL_NAME, entity.getName());
        template.update(INSERT, params, keyHolder, new String[]{COL_ID});
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new ClevertecException(EXC_MSG_CREATE);
        }
        Long id = key.longValue();
        return findById(id);
    }

    @Override
    public Tag findById(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_ID, id);
        try {
            return template.queryForObject(FIND_BY_ID, params, this::mapRow);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException(EXC_MSG_NOT_FOUND + id, e);
        }
    }

    @Override
    public List<Tag> find(String query) {
        return template.query(FIND_ALL, this::mapRow);
    }

    @Override
    public Tag update(Tag entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(COL_NAME, entity.getName()).addValue(PARAM_ID, entity.getId());
        int rowUpdated = template.update(UPDATE, params);
        if (rowUpdated == 0) {
            throw new ClientException(EXC_MSG_UPDATE + entity.getId());
        }
        return findById(entity.getId());
    }

    @Override
    public void delete(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_ID, id);
        if (template.update(DELETE, params) == 1) {
            throw new ClientException(EXC_MSG_DELETE + id);
        }
    }

    private Tag mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Tag tag = new Tag();
        tag.setId(resultSet.getLong(COL_ID));
        tag.setName(resultSet.getString(COL_NAME));
        return tag;
    }
}
