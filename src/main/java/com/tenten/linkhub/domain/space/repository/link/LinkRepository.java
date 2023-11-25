package com.tenten.linkhub.domain.space.repository.link;

import com.tenten.linkhub.domain.space.model.link.Link;
import com.tenten.linkhub.domain.space.repository.link.dto.LinkGetDto;
import com.tenten.linkhub.domain.space.repository.link.dto.LinkGetQueryCondition;
import org.springframework.data.domain.Slice;
import com.tenten.linkhub.domain.space.repository.link.dto.PopularLinkGetDto;

import java.util.List;
import java.util.Optional;

public interface LinkRepository {

    Link save(Link link);

    Link getById(Long linkId);

    Optional<Link> findById(Long linkId);

    Slice<LinkGetDto> getLinksByCondition(LinkGetQueryCondition condition);

    List<PopularLinkGetDto> getPopularLinks(Long memberId);

    Long countLinkBySpaceId(Long spaceId);

    List<Link> findBySpaceId(Long sourceSpaceId);

    Long bulkInsertLinks(List<Link> sourceLinks, Long spaceId, Long memberId);
}
