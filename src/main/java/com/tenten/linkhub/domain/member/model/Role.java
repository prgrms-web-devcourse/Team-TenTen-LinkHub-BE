package com.tenten.linkhub.domain.member.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    USER("USER", "일반 사용자 권한"),
    ADMIN("ADMIN", "관리자 권한");

    private final String code;
    private final String name;

}
