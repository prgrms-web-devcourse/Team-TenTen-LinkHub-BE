package com.tenten.linkhub.domain.space.service;

import com.tenten.linkhub.domain.space.exception.LinkViewHistoryException;
import com.tenten.linkhub.domain.space.model.link.Like;
import com.tenten.linkhub.domain.space.model.link.Link;
import com.tenten.linkhub.domain.space.model.link.LinkTag;
import com.tenten.linkhub.domain.space.model.link.LinkViewHistory;
import com.tenten.linkhub.domain.space.model.link.Tag;
import com.tenten.linkhub.domain.space.model.link.vo.Url;
import com.tenten.linkhub.domain.space.model.space.Space;
import com.tenten.linkhub.domain.space.repository.like.LikeRepository;
import com.tenten.linkhub.domain.space.repository.link.LinkRepository;
import com.tenten.linkhub.domain.space.repository.linkview.LinkViewRepository;
import com.tenten.linkhub.domain.space.repository.space.SpaceRepository;
import com.tenten.linkhub.domain.space.repository.tag.TagRepository;
import com.tenten.linkhub.domain.space.service.dto.link.LinkCreateRequest;
import com.tenten.linkhub.domain.space.service.mapper.LinkMapper;
import com.tenten.linkhub.global.exception.DataNotFoundException;
import com.tenten.linkhub.global.exception.UnauthorizedAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DefaultLinkService implements LinkService {
    private final LinkRepository linkRepository;
    private final TagRepository tagRepository;
    private final SpaceRepository spaceRepository;
    private final LikeRepository likeRepository;
    private final LinkViewRepository linkViewRepository;
    private final LinkMapper linkMapper;

    public DefaultLinkService(LinkRepository linkRepository, TagRepository tagRepository, SpaceRepository spaceRepository, LikeRepository likeRepository, LinkViewRepository linkViewRepository, LinkMapper linkMapper) {
        this.linkRepository = linkRepository;
        this.tagRepository = tagRepository;
        this.spaceRepository = spaceRepository;
        this.likeRepository = likeRepository;
        this.linkViewRepository = linkViewRepository;
        this.linkMapper = linkMapper;
    }

    @Override
    @Transactional
    public Long createLink(LinkCreateRequest request) {
        Space space = spaceRepository.getById(request.spaceId());
        Link link = Link.toLink(
                space,
                request.memberId(),
                request.title(),
                new Url(request.url()));

        Optional<Tag> tag = tagRepository.findBySpaceIdAndSpaceName(request.spaceId(), request.tagName());
        Tag newTag = Tag.toTag(space, request.tagName(), request.color());
        if (tag.isEmpty()) {
            tagRepository.save(newTag);
        }

        LinkTag linkTag = LinkTag.toLinkTag(link, tag.orElse(newTag));
        link.addLinkTag(linkTag);

        return linkRepository.save(link).getId();
    }

//    @Override
//    @Transactional
//    public Long updateLink(LinkUpdateRequest request) {
//        Space space = spaceRepository.getById(request.spaceId());
//        Link link = linkRepository.getById(request.linkId());
//        Optional<Tag> tag = toTag(space, link, request.tag(), request.color());
//
//        link.updateLink(new Url(request.url()), request.title(), tag);
//
//        return link.getId();
//    }
//
//    private Optional<Tag> toTag(Space space, Link link, String tagName, Color color) {
//        if (Objects.nonNull(tagName)) {
//            return Optional.of(Tag.toTag(space, link, tagName, color));
//        }
//        return Optional.empty();
//    }

    @Override
    @Transactional
    public Boolean createLike(Long linkId, Long memberId) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 링크입니다."));

        likeRepository.findByLinkIdAndMemberId(linkId, memberId)
                .ifPresent(m -> {
                    throw new UnauthorizedAccessException("이미 좋아요한 링크입니다.");
                });

        likeRepository.save(new Like(link, memberId));

        return true;
    }

    @Override
    @Transactional
    public void cancelLike(Long linkId, Long memberId) {
        Optional<Like> like = likeRepository.findByLinkIdAndMemberId(linkId, memberId);

        like.ifPresent(likeRepository::delete);
    }

    @Override
    @Transactional
    public void addLinkViewHistory(Long spaceId, Long linkId, Long memberId) {
        if (linkViewRepository.existsLinkView(linkId, memberId)) {
            throw new LinkViewHistoryException("이미 존재하는 접속 기록 데이터입니다.");
        }

        Link link = linkRepository.getById(linkId);
        LinkViewHistory linkViewHistory = LinkViewHistory.toLinkViewHistory(memberId, link);
        linkViewRepository.save(linkViewHistory);
    }

    @Override
    @Transactional
    public void deleteLink(Long linkId) {
        Link link = linkRepository.getById(linkId);

        link.deleteLink();
        linkViewRepository.deleteLinkViewHistory(linkId);
    }

//    @Override
//    public LinkGetByQueryResponses getLinks(LinksGetByQueryRequest request) {
//
//        Slice<LinkGetDto> linkGetDtos = linkRepository.getLinksByCondition(linkMapper.toQueryCondition(request);
//        LinkGetByQueryResponses responses = LinkGetByQueryResponses.from(linkGetDtos);
//    }
}
