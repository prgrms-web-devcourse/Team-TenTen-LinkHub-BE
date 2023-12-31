package com.tenten.linkhub.domain.member.repository.member;

import com.tenten.linkhub.domain.member.model.Member;
import com.tenten.linkhub.domain.member.model.Provider;
import com.tenten.linkhub.domain.member.repository.dto.MemberSearchQueryCondition;
import com.tenten.linkhub.domain.member.repository.dto.MemberWithProfileImageAndFollowingStatus;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    List<Member> findMemberWithProfileImageByMemberIds(List<Long> memberIds);

    boolean existsMemberByNewsEmail(String email);

    boolean existsMemberByNickname(String nickname);

    Optional<Member> findBySocialIdAndProvider(String socialId, Provider provider);

    Member save(Member newUser);

    Optional<Member> findByIdWithImageAndCategory(Long memberId);

    Member getById(Long memberId);

    List<Member> findMembersWithProfileImageAndCategoryByIds(List<Long> memberIds);

    Slice<MemberWithProfileImageAndFollowingStatus> searchMember(MemberSearchQueryCondition queryCond);

    Long findMemberIdByEmail(String email);
}
