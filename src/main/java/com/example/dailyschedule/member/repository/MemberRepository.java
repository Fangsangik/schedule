package com.example.dailyschedule.member.repository;

import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.schedule.entity.Schedule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public MemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Member createMember(Member member) {

        String sql = "insert into (userId, password, name, email, updated_at) from member" +
                "values(?,?,?,?,?)";

         jdbcTemplate.update(sql,
                member.getUserId(),
                member.getPassword(),
                member.getName(),
                member.getEmail(),
                member.getUpdatedAt());

         Long generatedId = jdbcTemplate.queryForObject("select LAST_INSERT_ID()", Long.class);
         return Member.builder()
                 .id(generatedId)
                 .userId(member.getUserId())
                 .password(member.getPassword())
                 .name(member.getName())
                 .email(member.getEmail())
                 .updatedAt(member.getUpdatedAt())
                 .build();
    }

    public Member findByUserId(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("해당 아이디를 찾을 수 없습니다.");
        }

        String sql = "select * from member where user_id = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{userId}, memberRowMapper());
    }

    public List<Member> findAll() {
        String sql = "select * from member";
        return jdbcTemplate.query(sql, memberRowMapper());
    }

    public Member findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("해당 id 값이 없습니다.");
        }

        String sql = "select * from member where id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, memberRowMapper());
    }

    public Member updateMember(Member member) {
        String sql = "update member set user_Id = ? password = ?, name = ?, email = ?, updated_at = ? where id = ?";

        int updateRows = jdbcTemplate.update(sql,
                member.getUserId(),
                member.getEmail(),
                member.getPassword(),
                member.getName(),
                member.getName(),
                member.getUpdatedAt());

        if (updateRows == 0) {
            throw new IllegalArgumentException("회원 정보를 update 하는데 실패했습니다.");
        }

        return member;
    }

    public void deleteMember(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("삭제하고자 하는 Id가 없습니다.");
        }

        String sql = "delete from member where id = ?";
        int delete = jdbcTemplate.update(sql, id);
        if (delete == 0) {
            throw new IllegalArgumentException("삭제에 실패했습니다.");
        }
    }

    public Member findByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("이름이 없습니다.");
        }

        String sql = "select * from member where name = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{name}, memberRowMapper());
    }

    private RowMapper<Member> memberRowMapper () {
        return (rs, rowNum) -> Member.builder()
                .userId(rs.getString("user_id"))
                .email(rs.getString("email"))
                .password(rs.getString("password"))
                .updatedAt(rs.getObject("updated_at", LocalDateTime.class))
                .build();
    }
}
