package ru.clevertec.ecl.data.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.data.repository.GiftCertificateRepository;
import ru.clevertec.ecl.data.repository.util.CriteriaQueryBuilder;

@Component
@RequiredArgsConstructor
@Transactional
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {

    private final CriteriaQueryBuilder criteriaBuilder;

    @PersistenceContext
    private EntityManager manager;

    @Override
    public List<GiftCertificate> findByParams(QueryParams params) {
        TypedQuery<GiftCertificate> query = criteriaBuilder.selectCertificateByParams(manager, params);
        return query.getResultList();
    }

    @Override
    public GiftCertificate findById(Long id) {
        Session session = manager.unwrap(Session.class);
        return session.find(GiftCertificate.class, id);
    }

    @Override
    public GiftCertificate update(GiftCertificate entity) {
        Session session = manager.unwrap(Session.class);
        session.flush();
        return entity;
    }

    @Override
    public void delete(Long id) {
        Session session = manager.unwrap(Session.class);
        GiftCertificate certificate = session.find(GiftCertificate.class, id);
        session.remove(certificate);
    }

    @Override
    public GiftCertificate create(GiftCertificate entity) {
        Session session = manager.unwrap(Session.class);
        session.persist(entity);
        return entity;
    }
}
