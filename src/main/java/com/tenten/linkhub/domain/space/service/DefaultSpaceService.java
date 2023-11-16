package com.tenten.linkhub.domain.space.service;

import com.tenten.linkhub.domain.space.model.space.Space;
import com.tenten.linkhub.domain.space.model.space.SpaceImage;
import com.tenten.linkhub.domain.space.model.space.SpaceMember;
import com.tenten.linkhub.domain.space.repository.space.SpaceRepository;
import com.tenten.linkhub.domain.space.repository.spacemember.SpaceMemberRepository;
import com.tenten.linkhub.domain.space.repository.tag.TagRepository;
import com.tenten.linkhub.domain.space.repository.tag.dto.TagInfo;
import com.tenten.linkhub.domain.space.service.dto.space.DeletedSpaceImageNames;
import com.tenten.linkhub.domain.space.service.dto.space.MySpacesFindRequest;
import com.tenten.linkhub.domain.space.service.dto.space.SpaceCreateRequest;
import com.tenten.linkhub.domain.space.service.dto.space.SpaceTagGetResponse;
import com.tenten.linkhub.domain.space.service.dto.space.SpaceTagGetResponses;
import com.tenten.linkhub.domain.space.service.dto.space.SpaceUpdateRequest;
import com.tenten.linkhub.domain.space.service.dto.space.SpaceWithSpaceImageAndSpaceMemberInfo;
import com.tenten.linkhub.domain.space.service.dto.space.SpacesFindByQueryRequest;
import com.tenten.linkhub.domain.space.service.dto.space.SpacesFindByQueryResponses;
import com.tenten.linkhub.domain.space.service.mapper.SpaceMapper;
import com.tenten.linkhub.global.exception.UnauthorizedAccessException;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.tenten.linkhub.domain.space.model.space.Role.OWNER;

@Service
public class DefaultSpaceService implements SpaceService {

    private final SpaceRepository spaceRepository;
    private final SpaceMapper mapper;
    private final SpaceMemberRepository spaceMemberRepository;
    private final TagRepository tagRepository;

    public DefaultSpaceService(SpaceRepository spaceRepository,
                               SpaceMapper mapper,
                               SpaceMemberRepository spaceMemberRepository,
                               TagRepository tagRepository) {
        this.spaceRepository = spaceRepository;
        this.mapper = mapper;
        this.spaceMemberRepository = spaceMemberRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public SpacesFindByQueryResponses findSpacesByQuery(SpacesFindByQueryRequest request) {
        Slice<Space> spaces = spaceRepository.findSpacesJoinSpaceImageByQuery(mapper.toQueryCond(request));

        return SpacesFindByQueryResponses.from(spaces);
    }

    @Override
    @Transactional
    public Long createSpace(SpaceCreateRequest request) {
        SpaceMember spaceMember = mapper.toSpaceMember(request, OWNER);
        SpaceImage spaceImage = mapper.toSpaceImage(request.imageInfo());

        Space space = mapper.toSpace(request, spaceMember, spaceImage);

        return spaceRepository.save(space).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public SpaceWithSpaceImageAndSpaceMemberInfo getSpaceWithSpaceImageAndSpaceMemberById(Long spaceId, Long memberId) {
        Space space = spaceRepository.getSpaceJoinSpaceMemberById(spaceId);

        space.validateVisibilityAndMembership(memberId);

        Boolean isOwner = space.isOwner(memberId);
        return SpaceWithSpaceImageAndSpaceMemberInfo.of(space, isOwner);
    }

    @Override
    @Transactional
    public Long updateSpace(SpaceUpdateRequest request) {
        Space space = spaceRepository.getById(request.spaceId());
        space.updateSpaceAttributes(mapper.toSpaceUpdateDto(request));

        return space.getId();
    }

    @Override
    public void checkMemberEditLink(Long memberId, Long spaceId) {
        if (!spaceMemberRepository.existsAuthorizedSpaceMember(memberId, spaceId)) {
            throw new UnauthorizedAccessException("링크를 생성할 수 있는 권한이 없습니다.");
        }
    }

    @Override
    @Transactional
    public DeletedSpaceImageNames deleteSpaceById(Long spaceId, Long memberId) {
        Space space = spaceRepository.getById(spaceId);
        space.deleteSpace(memberId);

        return DeletedSpaceImageNames.from(space.getAllSpaceImages());
    }

    @Override
    @Transactional(readOnly = true)
    public SpacesFindByQueryResponses findMySpacesByQuery(MySpacesFindRequest request) {
        Slice<Space> spaces = spaceRepository.findMySpacesJoinSpaceImageByQuery(mapper.toMySpacesFindQueryCondition(request));

        return SpacesFindByQueryResponses.from(spaces);
    }

    @Override
    public SpaceTagGetResponses getTagsBySpaceId(Long spaceId) {
        List<TagInfo> tagInfos = tagRepository.findBySpaceIdAndGroupBySpaceName(spaceId);
        List<SpaceTagGetResponse> tagResponses = tagInfos
                .stream()
                .map(t -> new SpaceTagGetResponse(t.name(), t.color().getValue()))
                .toList();

        return SpaceTagGetResponses.from(tagResponses);
    }

}
