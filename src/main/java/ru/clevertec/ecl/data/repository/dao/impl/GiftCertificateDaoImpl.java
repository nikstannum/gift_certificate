package ru.clevertec.ecl.data.repository.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.data.repository.dao.GiftCertificateDao;
import ru.clevertec.ecl.data.repository.util.CriteriaQueryBuilder;

@Component
@RequiredArgsConstructor
@Transactional
public class GiftCertificateDaoImpl implements GiftCertificateDao {

    private static final String COL_ID = "id";

    private final CriteriaQueryBuilder criteriaBuilder;

    @PersistenceContext
    private EntityManager manager;

    @Override
    public List<GiftCertificate> findAll(int limit, long offset) {
        Session session = manager.unwrap(Session.class);
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<GiftCertificate> criteriaQuery = cb.createQuery(GiftCertificate.class);
        Root<GiftCertificate> root = criteriaQuery.from(GiftCertificate.class);
        criteriaQuery.orderBy(cb.asc(root.get(COL_ID)));
        TypedQuery<GiftCertificate> query = session.createQuery(criteriaQuery);
        query.setFirstResult((int) offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

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

    private void updateFields(GiftCertificate fromDb, GiftCertificate certWithAcceptedParams) {
        if (certWithAcceptedParams.getName() != null) {
            fromDb.setName(certWithAcceptedParams.getName());
        }
        if (certWithAcceptedParams.getDescription() != null) {
            fromDb.setDescription(certWithAcceptedParams.getDescription());
        }
        if (certWithAcceptedParams.getPrice() != null) {
            fromDb.setPrice(certWithAcceptedParams.getPrice());
        }
        if (certWithAcceptedParams.getDuration() != null) {
            fromDb.setDuration(certWithAcceptedParams.getDuration());
        }
    }

    @Override
    public GiftCertificate update(GiftCertificate entity) {
        Session session = manager.unwrap(Session.class);
        GiftCertificate fromDb = session.find(GiftCertificate.class, entity.getId());
        updateFields(fromDb, entity);
        session.flush();
        return fromDb;
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
        session.refresh(entity);
        return entity;
    }
}
