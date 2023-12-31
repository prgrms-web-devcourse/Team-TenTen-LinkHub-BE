package com.tenten.linkhub.domain.space.controller.dto.space;

import com.tenten.linkhub.domain.space.model.category.Category;

public record PublicSpaceFindWithFilterApiResponse(
        Long spaceId,
        String spaceName,
        String description,
        Category category,
        Long viewCount,
        Long scrapCount,
        Long favoriteCount,
        String spaceImagePath,
        String ownerNickName
) {
}
