package com.tenten.linkhub.domain.member.service.dto;

import com.tenten.linkhub.domain.member.model.Member;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record MemberInfos(Map<Long, MemberInfo> memberInfos) {

    public static MemberInfos from(List<Member> members) {
        Map<Long, MemberInfo> memberInfos = members.stream()
                .collect(Collectors.toMap(
                        Member::getId,
                        m -> new MemberInfo(
                                m.getNickname(),
                                m.getAboutMe(),
                                m.retrieveProfileImages().isEmpty() ? null : m.retrieveProfileImages().get(0).getPath()
                        )
                ));

        return new MemberInfos(memberInfos);
    }

}
