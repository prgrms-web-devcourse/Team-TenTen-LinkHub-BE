package com.tenten.linkhub.domain.space.service;

import com.tenten.linkhub.domain.space.model.space.Space;
import com.tenten.linkhub.domain.space.repository.space.SpaceRepository;
import com.tenten.linkhub.domain.space.repository.spacemember.SpaceMemberRepository;
import com.tenten.linkhub.domain.space.service.dto.space.SpaceCreateRequest;
import com.tenten.linkhub.domain.space.service.dto.space.SpaceGetDto;
import com.tenten.linkhub.domain.space.service.dto.space.SpaceUpdateRequest;
import com.tenten.linkhub.domain.space.service.dto.space.SpaceWithSpaceImageAndSpaceMemberInfo;
import com.tenten.linkhub.domain.space.service.dto.space.SpacesFindByQueryRequest;
import com.tenten.linkhub.domain.space.service.dto.space.SpacesFindByQueryResponses;
import com.tenten.linkhub.domain.space.service.mapper.SpaceMapper;
import com.tenten.linkhub.global.exception.UnauthorizedAccessException;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.tenten.linkhub.domain.space.model.space.Role.OWNER;

@Service
public class DefaultSpaceService implements SpaceService {

    private final SpaceRepository spaceRepository;
    private final SpaceMapper mapper;
    private final SpaceMemberRepository spaceMemberRepository;

    public DefaultSpaceService(SpaceRepository spaceRepository, SpaceMapper mapper, SpaceMemberRepository spaceMemberRepository) {
        this.spaceRepository = spaceRepository;
        this.mapper = mapper;
        this.spaceMemberRepository = spaceMemberRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public SpacesFindByQueryResponses findSpacesByQuery(SpacesFindByQueryRequest request) {
        Slice<Space> spaces = spaceRepository.findSpaceWithSpaceImageByQuery(mapper.toQueryCond(request));

        return SpacesFindByQueryResponses.from(spaces);
    }

    @Override
    @Transactional
    public Long createSpace(SpaceCreateRequest request) {
        Space space = mapper.toSpace(request);

        space.addSpaceMember(
                mapper.toSpaceMember(request, OWNER)
        );

        space.addSpaceImage(
                mapper.toSpaceImage(request.imageInfo())
        );

        return spaceRepository.save(space).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public SpaceWithSpaceImageAndSpaceMemberInfo getSpaceWithSpaceImageAndSpaceMemberById(Long spaceId, Long memberId) {
        Space space = spaceRepository.getSpaceJoinSpaceMemberById(spaceId);

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
    public void checkMemberAddLink(Long memberId, Long spaceId) {
        if (!spaceMemberRepository.existsAuthorizedSpaceMember(memberId, spaceId)) {
            throw new UnauthorizedAccessException("링크를 생성할 수 있는 권한이 없습니다.");
        }
    }

    @Override
    public SpaceGetDto getSpace(Long spaceId) {
        return new SpaceGetDto(spaceRepository.getById(spaceId));
    }

}
