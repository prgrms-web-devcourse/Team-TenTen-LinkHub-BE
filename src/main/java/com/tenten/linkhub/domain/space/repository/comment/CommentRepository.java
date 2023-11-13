package com.tenten.linkhub.domain.space.repository.comment;

import com.tenten.linkhub.domain.space.model.space.Comment;
import com.tenten.linkhub.domain.space.repository.comment.dto.CommentAndChildCommentCount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CommentRepository {

    Comment save(Comment comment);

    Slice<CommentAndChildCommentCount> findCommentAndChildCommentCountBySpaceId(Long spaceId, Pageable pageable);
}