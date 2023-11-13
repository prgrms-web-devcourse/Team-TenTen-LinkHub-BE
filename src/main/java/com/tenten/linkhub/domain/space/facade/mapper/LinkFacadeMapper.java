package com.tenten.linkhub.domain.space.facade.mapper;

import com.tenten.linkhub.domain.space.facade.dto.LinkCreateFacadeRequest;
import com.tenten.linkhub.domain.space.service.dto.link.LinkCreateRequest;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface LinkFacadeMapper {
    @Mapping(source = "spaceId", target = "spaceId")
    @Mapping(source = "memberId", target = "memberId")
    LinkCreateRequest toLinkCreateRequest(LinkCreateFacadeRequest request, Long memberId, Long spaceId);
}