package com.tenten.linkhub.domain.space.repository.space.dto;

import com.tenten.linkhub.domain.space.model.category.Category;
import org.springframework.data.domain.Pageable;

public record MySpacesFindQueryCondition(
        Pageable pageable,
        String keyWord,
        Category filter,
        Long memberId
) {
}