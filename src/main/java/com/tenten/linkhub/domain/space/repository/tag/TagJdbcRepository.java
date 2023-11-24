package com.tenten.linkhub.domain.space.repository.tag;

import com.tenten.linkhub.domain.space.model.link.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class TagJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public Long bulkInsertTags(List<Tag> targetTags, Long spaceId) {
        String sql = "INSERT INTO tags (space_id, name, color) " +
                "VALUES (?, ?, ?) ";

        jdbcTemplate.batchUpdate(sql,
                targetTags,
                targetTags.size(),
                (PreparedStatement ps, Tag tag) -> {
                    ps.setLong(1, spaceId);
                    ps.setString(2, tag.getName());
                    ps.setString(3, tag.getColor().toString());
                });

        return jdbcTemplate.queryForObject("SELECT last_insert_id()", Long.class);
    }

}
