package com.tenten.linkhub.domain.member.controller.mapper;

import com.tenten.linkhub.domain.member.controller.dto.MailVerificationApiRequest;
import com.tenten.linkhub.domain.member.controller.dto.MailVerificationApiResponse;
import com.tenten.linkhub.domain.member.controller.dto.MemberJoinApiRequest;
import com.tenten.linkhub.domain.member.service.dto.MailVerificationRequest;
import com.tenten.linkhub.domain.member.service.dto.MailVerificationResponse;
import com.tenten.linkhub.domain.member.service.dto.MemberJoinRequest;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface MemberApiMapper {

    MailVerificationRequest toMailVerificationRequest(MailVerificationApiRequest request);

    MailVerificationApiResponse toMailVerificatonApiResponse(MailVerificationResponse response);

    MemberJoinRequest toMemberJoinRequest(MemberJoinApiRequest request, MultipartFile file);
}
