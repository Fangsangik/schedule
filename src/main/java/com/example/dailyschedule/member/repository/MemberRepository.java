package com.example.dailyschedule.member.repository;

import com.example.dailyschedule.error.CustomException;
import com.example.dailyschedule.member.entity.Member;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.dailyschedule.error.type.ErrorCode.*;

//MemberRepository
@Repository
public class MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public MemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //생성
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

    //userId 조회
    public Member findByUserId(String userId) {
        String selectMemberSql = "SELECT * FROM member WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(selectMemberSql, new Object[]{userId}, (rs, rowNum) ->
                    Member.builder()
                            .id(rs.getLong("id"))
                            .userId(rs.getString("user_id"))
                            .password(rs.getString("password"))
                            .name(rs.getString("name"))
                            .email(rs.getString("email"))
                            .updatedAt(rs.getDate("updated_at"))
                            .build()
            );
        } catch (EmptyResultDataAccessException e) {
            return null; // 사용자가 없으면 null 반환
        }
    }


    //전체 조회
    public List<Member> findAll() {
        String sql = "select * from member";
        return jdbcTemplate.query(sql, memberRowMapper());
    }

    //아이디 조회
    public Member findById(Long id) {
        if (id == null) {
            throw new CustomException(ID_NOT_FOUND);
        }

        String sql = "select * from member where id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, memberRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null; // 또는 Optional.empty()로 반환하여 예외 처리
        }
    }

    //회원 수정
    public Member updateMember(Member member) {
        String sql = "update member set user_Id = ?, password = ?, name = ?, email = ?, updated_at = ? where id = ?";

        int updateRows = jdbcTemplate.update(sql,
                member.getUserId(),
                member.getPassword(),
                member.getName(),
                member.getEmail(),
                member.getUpdatedAt(),
                member.getId());

        if (updateRows == 0) {
            throw new IllegalArgumentException("회원 정보를 update 하는데 실패했습니다.");
        }

        return member;
    }

    //삭제
    public void deleteMember(Long id) {
        if (id == null) {
            throw new CustomException(ID_NOT_FOUND);
        }

        String sql = "delete from member where id = ?";
        int delete = jdbcTemplate.update(sql, id);
        if (delete == 0) {
            throw new CustomException(DELETE_FAILED);
        }
    }

    //이름 조회
    public Member findByName(String name) {
        if (name == null) {
            throw new CustomException(NOT_FOUND);
        }

        String sql = "select * from member where name = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{name}, memberRowMapper());
    }

    //삭제
    public void deleteMember() {
        String memberSql = "delete from member";
        int memberDeletedCount = jdbcTemplate.update(memberSql);

        if (memberDeletedCount == 0) {
            throw new CustomException(DELETE_FAILED);

        }
    }

    //회원과 스케줄 동시 삭제
    public void deleteMemberAndSchedule() {

        String memberSql = "delete from member";
        String scheduleSql = "delete from schedule";

        // 각 삭제 작업에 대해 영향을 받은 행 수 확인
        int memberDeletedCount = jdbcTemplate.update(memberSql);
        int scheduleDeletedCount = jdbcTemplate.update(scheduleSql);

        // 삭제된 행이 없으면 예외 발생
        if (memberDeletedCount == 0 && scheduleDeletedCount == 0) {
            throw new IllegalArgumentException("삭제할 데이터가 없습니다.");
        }
    }

    //MemberRowMapper
    private RowMapper<Member> memberRowMapper () {
        return (rs, rowNum) -> Member.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .userId(rs.getString("user_id"))
                .email(rs.getString("email"))
                .password(rs.getString("password"))
                .updatedAt(rs.getDate("updated_at"))
                .build();
    }
}
