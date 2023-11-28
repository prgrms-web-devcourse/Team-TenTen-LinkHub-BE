package com.tenten.linkhub.global.response;

import lombok.Getter;

@Getter
public enum ErrorCode {

    //global
    INTERNAL_SERVER_ERROR("G001", "Internal Server Error"),
    UNAUTHENTICATED_ERROR("G002", "Requires Authentication"),

    //s3 - image
    FAIL_TO_UPLOAD_IMAGES("S001", "이미지를 업로드 할 수 없습니다."),
    FAIL_TO_DELETE_IMAGE("S002", "이미지를 삭제할 수 없습니다."),

    //member
    DUPLICATE_NEWS_EMAIL("M001", "중복된 이메일입니다."),
    DUPLICATE_FOLLOWING("M002", "중복된 팔로잉입니다."),
    DUPLICATE_SOCIAL_ID("M003", "중복된 소셜 아이디입니다."),
    DUPLICATE_NICKNAME("M004", "중복된 닉네임입니다."),

    //favorite
    DUPLICATE_FAVORITE("F001", "중복된 즐겨찾기 등록입니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
