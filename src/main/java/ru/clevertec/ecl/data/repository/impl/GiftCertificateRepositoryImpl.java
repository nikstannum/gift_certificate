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
import ru.clevertec.ecl.data.repository.util.QueryBuilder;

@Repository
@RequiredArgsConstructor
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {
    private final GiftCertificateDao giftCertificateDao;
    private final TagDao tagDao;
    private final QueryBuilder queryBuilder;

    @Override
    public Optional<Tag> findTagByName(String name) {
        return tagDao.findTagByName(name);
    }

    @Override
    public void createCertificateTagEntry(Long certificateId, Long tagId) {
        giftCertificateDao.createCertificateTagEntry(certificateId, tagId);
    }

    @Override
    public List<Tag> findTagsByCertificateId(Long id) {
        return tagDao.findTagsByGiftCertificateId(id);
    }

    @Override
    public GiftCertificate updateByParams(QueryParams params, Long id) {
        String queryCert = queryBuilder.buildQueryCertificateUpdate(params);
        return giftCertificateDao.updateByParams(queryCert, id);
    }

    @Override
    public Tag createTag(Tag tag) {
        return tagDao.create(tag);
    }

    @Override
    public List<GiftCertificate> find(QueryParams queryParams) {
        String query = queryBuilder.buildQuerySelect(queryParams);
        if (queryParams.getTag() == null) {
            List<GiftCertificate> list = giftCertificateDao.find(query);
            list.forEach(cert -> cert.setTags(tagDao.findTagsByGiftCertificateId(cert.getId())));
            return list;
        } else {
            return giftCertificateDao.findByRichParams(query);
        }
    }

    @Override
    public GiftCertificate create(GiftCertificate entity) {
        return giftCertificateDao.create(entity);
    }

    @Override
    public GiftCertificate createByParams(QueryParams params) {
        String queryCert = queryBuilder.buildQueryCertificateCreate(params);
        return giftCertificateDao.createByParams(queryCert);
    }

    @Override
    public GiftCertificate findById(Long id) {
        GiftCertificate certificate = giftCertificateDao.findById(id);
        List<Tag> tags = tagDao.findTagsByGiftCertificateId(id);
        certificate.setTags(tags);
        return certificate;
    }

    @Override
    public GiftCertificate update(GiftCertificate entity) {
        return giftCertificateDao.update(entity);
    }

    @Override
    public void delete(Long id) {
        giftCertificateDao.deleteCertificateTagByCertificateId(id);
        giftCertificateDao.delete(id);
    }
}
