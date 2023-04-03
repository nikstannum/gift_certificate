package ru.clevertec.ecl.data.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.data.repository.TagRepository;

@Component
@RequiredArgsConstructor
@Transactional
public class TagRepositoryImpl implements TagRepository {
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
