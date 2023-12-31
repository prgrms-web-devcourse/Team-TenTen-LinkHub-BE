package com.tenten.linkhub.domain.space.service;

import com.tenten.linkhub.domain.space.exception.LinkViewHistoryException;
import com.tenten.linkhub.domain.space.model.link.Color;
import com.tenten.linkhub.domain.space.model.link.Like;
import com.tenten.linkhub.domain.space.model.link.Link;
import com.tenten.linkhub.domain.space.model.link.LinkTag;
import com.tenten.linkhub.domain.space.model.link.LinkViewHistory;
import com.tenten.linkhub.domain.space.model.link.Tag;
import com.tenten.linkhub.domain.space.model.link.vo.Url;
import com.tenten.linkhub.domain.space.model.space.Space;
import com.tenten.linkhub.domain.space.repository.like.LikeRepository;
import com.tenten.linkhub.domain.space.repository.link.LinkRepository;
import com.tenten.linkhub.domain.space.repository.link.dto.LinkGetDto;
import com.tenten.linkhub.domain.space.repository.link.dto.PopularLinkGetDto;
import com.tenten.linkhub.domain.space.repository.linktag.LinkTagRepository;
import com.tenten.linkhub.domain.space.repository.linkview.LinkViewRepository;
import com.tenten.linkhub.domain.space.repository.space.SpaceRepository;
import com.tenten.linkhub.domain.space.repository.tag.TagRepository;
import com.tenten.linkhub.domain.space.service.dto.link.LinkCreateRequest;
import com.tenten.linkhub.domain.space.service.dto.link.LinkGetByQueryResponses;
import com.tenten.linkhub.domain.space.service.dto.link.LinkUpdateRequest;
import com.tenten.linkhub.domain.space.service.dto.link.LinksGetByQueryRequest;
import com.tenten.linkhub.domain.space.service.dto.link.PopularLinksGetByQueryResponses;
import com.tenten.linkhub.domain.space.service.mapper.LinkMapper;
import com.tenten.linkhub.global.exception.DataDuplicateException;
import com.tenten.linkhub.global.exception.DataNotFoundException;
import com.tenten.linkhub.global.exception.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.tenten.linkhub.global.response.ErrorCode.DUPLICATE_LINK_RIKE;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DefaultLinkService implements LinkService {
    private final LinkRepository linkRepository;
    private final TagRepository tagRepository;
    private final SpaceRepository spaceRepository;
    private final LikeRepository likeRepository;
    private final LinkViewRepository linkViewRepository;
    private final LinkTagRepository linkTagRepository;
    private final LinkMapper linkMapper;

    @Override
    @Transactional
    public Long createLink(LinkCreateRequest request) {
        Space space = spaceRepository.getById(request.spaceId());
        Link link = Link.toLink(
                space,
                request.memberId(),
                request.title(),
                new Url(request.url()));

        if (request.hasCreateTagInfo()) { //태그 정보를 포함하여 링크를 생성할 경우
            Optional<Tag> tag = tagRepository.findBySpaceIdAndTagName(request.spaceId(), request.tagName());
            Tag newTag = Tag.toTag(space, request.tagName(), Color.toColor(request.color()));
            if (tag.isEmpty()) {
                tagRepository.save(newTag);
            }

            LinkTag linkTag = LinkTag.toLinkTag(link, tag.orElse(newTag));
            link.addLinkTag(linkTag);
        }
        return linkRepository.save(link).getId();
    }

    @Override
    @Transactional
    public Long updateLink(LinkUpdateRequest request) {
        Space space = spaceRepository.getById(request.spaceId());
        Link link = linkRepository.getById(request.linkId());

        if (request.hasUpdateTagInfo()) { //태그 정보를 포함하여 링크를 수정할 경우
            Optional<Tag> tag = tagRepository.findBySpaceIdAndTagName(request.spaceId(), request.tagName());
            Tag newTag = Tag.toTag(space, request.tagName(), Color.toColor(request.color()));

            if (tag.isEmpty()) {
                tagRepository.save(newTag);
            }

            link.updateLink(
                    new Url(request.url()),
                    request.title(),
                    LinkTag.toLinkTag(link, tag.orElse(newTag))
            );
        } else {
            link.updateLink(new Url(request.url()), request.title());
        }

        return link.getId();
    }

    @Override
    @Transactional
    public Boolean createLike(Long linkId, Long memberId) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 링크입니다."));

        likeRepository.findByLinkIdAndMemberId(linkId, memberId)
                .ifPresent(m -> {
                    throw new DataDuplicateException(DUPLICATE_LINK_RIKE);
                });

        likeRepository.save(new Like(link, memberId));

        return true;
    }

    @Override
    @Transactional
    public void cancelLike(Long linkId, Long memberId) {
        Optional<Like> like = likeRepository.findByLinkIdAndMemberId(linkId, memberId);

        likeRepository.delete(like.orElseThrow(() -> new DataNotFoundException("존재하지 않는 좋아요입니다.")));
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

    @Override
    public LinkGetByQueryResponses getLinks(LinksGetByQueryRequest request) {
        Slice<LinkGetDto> linkGetDtos = linkRepository.getLinksByCondition(linkMapper.toQueryCondition(request));
        LinkGetByQueryResponses responses = LinkGetByQueryResponses.from(linkGetDtos);
        return responses;
    }

    @Override
    public PopularLinksGetByQueryResponses getPopularLinks(Long memberId) {
        List<PopularLinkGetDto> popularLinks = linkRepository.getPopularLinks(memberId);
        PopularLinksGetByQueryResponses responses = PopularLinksGetByQueryResponses.from(popularLinks);
        return responses;
    }

    @Override
    @Transactional
    public void copyLinkBySpaceIdAndPaste(Long sourceSpaceId, Long savedSpaceId, Long memberId) {
        List<Link> sourceLinks = linkRepository.findBySpaceId(sourceSpaceId);

        List<Long> sourceLinkIds = sourceLinks
                .stream()
                .map(Link::getId)
                .toList();

        List<LinkTag> sourceLinkTags = linkTagRepository.findByLinkIdIn(sourceLinkIds);
        List<Tag> sourceTags = tagRepository.findBySpaceId(sourceSpaceId);

        Long insertedLinksFirstId = linkRepository.bulkInsertLinks(sourceLinks, savedSpaceId, memberId);
        Map<Long, Long> linkIdMappingMap = linkMapper.toCopyAndPasteLinkIdMap(sourceLinks, insertedLinksFirstId);

        Long insertedTagsFirstId = tagRepository.bulkInsertTags(sourceTags, savedSpaceId);
        Map<Long, Long> tagIdMappingMap = linkMapper.toCopyAndPasteTagIdMap(sourceTags, insertedTagsFirstId);

        linkTagRepository.bulkInsertLinkTag(sourceLinkTags, linkIdMappingMap, tagIdMappingMap);
    }

}
