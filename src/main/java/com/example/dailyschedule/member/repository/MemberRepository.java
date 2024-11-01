package com.example.dailyschedule.member.repository;

import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.schedule.entity.Schedule;
import org.springframework.dao.EmptyResultDataAccessException;
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

        String sql = "INSERT INTO member (user_id, password, name, email, updated_at) VALUES (?, ?, ?, ?, ?)";

         jdbcTemplate.update(sql,
                member.getUserId(),
                member.getPassword(),
                member.getName(),
                member.getEmail(),
                member.getUpdatedAt());

         //jdbcTemplate.queryForObject를 사용하여 마지막에 삽입된 레코드의 ID를 Long 타입으로 가져오는 것
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
        //memberRepo 쪽으로 빼서 사용
        String selectMemberSql = "SELECT * FROM member WHERE user_id = ?";
        Member member;
        try {

            //검증 단계에서 다 가져올 필요 없음
            member = jdbcTemplate.queryForObject(selectMemberSql, new Object[]{userId}, (rs, rowNum) ->
                    Member.builder()
                            .id(rs.getLong("id"))
                            .userId(rs.getString("user_id"))
                            .password(rs.getString("password"))
                            .name(rs.getString("name"))
                            .email(rs.getString("email"))
                            .updatedAt(rs.getObject("updated_at", LocalDateTime.class))
                            .build()
            );

        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("해당 사용자 이름을 찾을 수 없습니다: " + userId);
        }
        return member;
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

    public void deleteMemberAndSchedule() {
        String deleteMemberSql = "delete from member";
        jdbcTemplate.update(deleteMemberSql);

        String deleteScheduleSql = "delete from schedule";
        jdbcTemplate.update(deleteScheduleSql);
    }

    public Member findByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("이름이 없습니다.");
        }

        String sql = "select * from member where name = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{name}, memberRowMapper());
    }

    public void deleteAll() {
        String sql = "delete from member";
        int delete = jdbcTemplate.update(sql);
        if (delete < 0) {
            throw new IllegalArgumentException("삭제하는데 실패했습니다.");
        }
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
