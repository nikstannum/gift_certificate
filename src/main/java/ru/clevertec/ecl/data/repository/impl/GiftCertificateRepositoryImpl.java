package ru.clevertec.ecl.data.repository.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.data.entity.GiftCertificate;
import ru.clevertec.ecl.data.entity.QueryParams;
import ru.clevertec.ecl.data.entity.Tag;
import ru.clevertec.ecl.data.repository.GiftCertificateRepository;
import ru.clevertec.ecl.data.repository.dao.GiftCertificateDao;
import ru.clevertec.ecl.data.repository.dao.TagDao;

@Repository
@RequiredArgsConstructor
@Transactional
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {

    private final GiftCertificateDao giftCertificateDao;
    private final TagDao tagDao;

    @Override
    public Optional<Tag> findTagByName(String name) {
        return tagDao.findTagByName(name);
    }


    @Override
    public Tag createTag(Tag tag) {
        return tagDao.create(tag);
    }

    @Override
    public List<GiftCertificate> find(QueryParams queryParams) {
        return giftCertificateDao.findByParams(queryParams);
    }

    @Override
    public GiftCertificate create(GiftCertificate entity) {
        return giftCertificateDao.create(entity);
    }

    @Override
    public GiftCertificate findById(Long id) {
        return giftCertificateDao.findById(id);
    }

    @Override
    public GiftCertificate update(GiftCertificate entity) {
        return giftCertificateDao.update(entity);
    }

    @Override
    public void delete(Long id) {
        giftCertificateDao.delete(id);
    }
}
