package ru.clevertec.ecl.data.repository.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.data.repository.dao.TagDao;

@Component
@RequiredArgsConstructor
@Transactional
public class TagDaoImpl implements TagDao {
    private static final String COL_ID = "id";

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Optional<Tag> findTagByName(String tagName) {
        Session session = manager.unwrap(Session.class);
        TypedQuery<Tag> query = session.createQuery("from Tag where name=:tagName", Tag.class);
        query.setParameter("tagName", tagName);
        try {
            Tag tag = query.getSingleResult();
            return Optional.of(tag);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Tag create(Tag entity) {
        Session session = manager.unwrap(Session.class);
        session.persist(entity);
        return entity;
    }

    @Override
    public Tag findById(Long id) {
        Session session = manager.unwrap(Session.class);
        return session.find(Tag.class, id);
    }

    @Override
    public List<Tag> findAll(int limit, long offset) {
        Session session = manager.unwrap(Session.class);
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Tag> criteriaQuery = cb.createQuery(Tag.class);
        Root<Tag> root = criteriaQuery.from(Tag.class);
        criteriaQuery.orderBy(cb.asc(root.get(COL_ID)));
        TypedQuery<Tag> query = session.createQuery(criteriaQuery);
        query.setFirstResult((int) offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public Tag update(Tag entity) {
        Session session = manager.unwrap(Session.class);
        Tag fromDb = session.find(Tag.class, entity.getId());
        fromDb.setName(entity.getName());
        return fromDb;
    }

    @Override
    public void delete(Long id) {
        Session session = manager.unwrap(Session.class);
        Tag tag = session.find(Tag.class, id);
        session.remove(tag);
    }
}
