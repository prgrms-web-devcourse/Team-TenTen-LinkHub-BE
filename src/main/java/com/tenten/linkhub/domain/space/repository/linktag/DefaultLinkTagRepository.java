package com.tenten.linkhub.domain.space.repository.linktag;

import com.tenten.linkhub.domain.space.model.link.LinkTag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class DefaultLinkTagRepository implements LinkTagRepository {

    private final LinkTagJpaRepository linkTagJpaRepository;
    private final LinkTagJdbcRepository linkTagJdbcRepository;

    @Override
    public List<LinkTag> findByLinkIdIn(List<Long> sourceLinkIds) {
        return linkTagJpaRepository.findByLinkIdsInAndIsDeletedFalse(sourceLinkIds);
    }

    @Override
    public Long bulkInsertLinkTag(List<LinkTag> sourceLinkTags, Map<Long, Long> linkIdMappingMap, Map<Long, Long> tagIdMappingMap) {
        return linkTagJdbcRepository.bulkInsertLinkTag(sourceLinkTags, linkIdMappingMap, tagIdMappingMap);
    }

}
