package com.tenten.linkhub.domain.space.controller.dto.link;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;

public record LinkCreateApiRequest(
        @NotBlank(message = "URL은 빈 값이 들어올 수 없습니다.")
        @URL(message = "적절한 URL 형식이 아닙니다.")
        String url,

        @NotBlank(message = "title은 빈 값이 들어올 수 없습니다.")
        String title,

        @Pattern(regexp = "^(?!\\s*$).+", message = "태그는 비어있거나 공백만 있을 수 없습니다.")
        String tag
) {
}
