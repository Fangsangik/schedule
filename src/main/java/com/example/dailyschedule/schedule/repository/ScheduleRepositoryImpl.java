package com.example.dailyschedule.schedule.repository;

import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.schedule.dto.SearchDto;
import com.example.dailyschedule.schedule.entity.Schedule;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class ScheduleRepositoryImpl {

    private final JdbcTemplate jdbcTemplate;

    public ScheduleRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // userId 제거
    public Schedule createSchedule(Schedule schedule, Member member) {

        String sql = "INSERT INTO schedule (author, title, created_at, password, description, updated_at, deleted_at, member_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                schedule.getAuthor(),
                schedule.getTitle(),
                schedule.getCreatedAt(),
                schedule.getPassword(),
                schedule.getDescription(),
                schedule.getUpdatedAt(),
                schedule.getDeletedAt(),
                member.getId() // Member 객체에서 member_id를 가져옴
        );

        // 최근 추가된 ID 조회
        Long generatedId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

        return Schedule.builder()
                .id(generatedId)
                .author(schedule.getAuthor())
                .title(schedule.getTitle())
                .password(schedule.getPassword())
                .description(schedule.getDescription())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .deletedAt(schedule.getDeletedAt())
                .member(member) // Member 객체 설정
                .build();
    }

    public Schedule updateSchedule(Member member, Schedule schedule) {
        if (schedule.getId() == null) {
            throw new IllegalArgumentException("해당 id가 존재하지 않습니다.");
        }

        String sql = "UPDATE schedule SET title = ?, author = ?, description = ?, updated_at = ?, password = ?, created_at = ?, deleted_at = ?, member_id = ? WHERE id = ?";

        int updatedRows = jdbcTemplate.update(sql,
                schedule.getTitle(),
                schedule.getAuthor(),
                schedule.getDescription(),
                schedule.getUpdatedAt(),
                schedule.getPassword(),
                schedule.getCreatedAt(),
                schedule.getDeletedAt(),
                member.getId(),
                schedule.getId());  // 마지막 파라미터로 id 추가

        if (updatedRows == 0) {
            throw new IllegalArgumentException("업데이트 실패");
        }
        return schedule;
    }


    public Schedule findScheduleById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id 값이 null 입니다");
        }

        String sql = """
                    SELECT s.*, 
                           m.id AS member_id, m.user_id AS user_id, m.password AS member_password, 
                           m.name AS member_name, m.email AS member_email, m.updated_at AS member_updated_at
                      FROM schedule s
                      LEFT JOIN member m ON s.member_id = m.id
                     WHERE s.id = ?
                """;

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, scheduleRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null; // 조회 결과가 없을 경우 null 반환
        }
    }


    public void deleteScheduleById(Long scheduleId) {

        String sql = "delete from schedule where id = ?";

        int delete = jdbcTemplate.update(sql, scheduleId);

        if (delete == 0) {
            throw new IllegalArgumentException("삭제 살패했습니다.");
        }
    }


    //test용
    public void deleteAll() {
        String sql = "delete from schedule";

        int deleteAll = jdbcTemplate.update(sql);
        if (deleteAll == 0) {
            throw new IllegalArgumentException("전체 삭제에 실패했습니다.");
        }
    }


    public Page<Schedule> findAllOrderByUpdatedDateDesc(SearchDto searchDto) {

        int limit = searchDto.getLimit();
        int offset = searchDto.getOffset();

        // 전체 레코드 수를 계산하는 쿼리
        String countSql = "SELECT COUNT(*) FROM schedule";
        Long totalCount = jdbcTemplate.queryForObject(countSql, Long.class);

        if (totalCount == 0 || offset >= totalCount) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(offset / limit, limit), totalCount);
        }

        String sql = """
                SELECT s.*, 
                       m.id AS member_id, m.user_id AS user_id, m.password AS member_password, 
                       m.name AS member_name, m.email AS member_email, m.updated_at AS member_updated_at
                  FROM schedule s
                  LEFT JOIN member m ON s.member_id = m.id
                 ORDER BY s.updated_at DESC
                 LIMIT ? OFFSET ?
            """;

        List<Schedule> schedules = jdbcTemplate.query(sql, new Object[]{limit, offset}, scheduleRowMapper());

        return new PageImpl<>(schedules, PageRequest.of(offset / limit, limit), totalCount);
    }



    public Page<Schedule> findSchedulesByUpdatedDateAndAuthor(Date updatedAt, String author, SearchDto searchDto) {
        int limit = searchDto.getLimit();
        int offset = searchDto.getOffset();

        StringBuilder countSql = new StringBuilder("""
        SELECT COUNT(*)
          FROM schedule s
          LEFT JOIN member m ON s.member_id = m.id
          WHERE 1=1
    """);

        StringBuilder sql = new StringBuilder("""
        SELECT s.*, 
               m.id AS member_id, m.user_id AS user_id, m.password AS member_password, 
               m.name AS member_name, m.email AS member_email, m.updated_at AS member_updated_at
          FROM schedule s
          LEFT JOIN member m ON s.member_id = m.id
          WHERE 1=1
    """);

        List<Object> params = new ArrayList<>();

        if (updatedAt != null) {
            countSql.append(" AND DATE(s.updated_at) = DATE(?)");
            sql.append(" AND DATE(s.updated_at) = DATE(?)");
            params.add(updatedAt);
        }

        if (author != null) {
            countSql.append(" AND s.author = ?");
            sql.append(" AND s.author = ?");
            params.add(author);
        }

        sql.append(" ORDER BY s.updated_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        int total = jdbcTemplate.queryForObject(countSql.toString(), params.subList(0, params.size() - 2).toArray(), Integer.class);

        if (total == 0 || offset >= total) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(offset / limit, limit), total);
        }

        List<Schedule> schedules = jdbcTemplate.query(sql.toString(), params.toArray(), scheduleRowMapper());

        return new PageImpl<>(schedules, PageRequest.of(offset / limit, limit), total);
    }

    //선텍 일정 조회
    public Schedule findDateById(Long id, String field, Date date) {
        if (date == null) {
            throw new IllegalArgumentException("해당 날짜가 없습니다.");
        }

        if (!field.equals("created_at") && !field.equals("updated_at") && !field.equals("deleted_at")) {
            throw new IllegalArgumentException("유요하지 않은 필드 이름입니다");
        }

        String sql = String.format("select * from schedule where %s = ? and id = ?", field);

        // query 메서드를 사용하여 다수의 결과를 리스트로 반환
        return jdbcTemplate.queryForObject(sql, new Object[]{date, id}, simpleScheduleRowMapper());
    }


    public Page<Schedule> findByDate(Date date, SearchDto searchDto) {
        if (date == null) {
            throw new IllegalArgumentException("해당 날짜가 없습니다.");
        }

        int limit = searchDto.getLimit();
        int offset = searchDto.getOffset();

        if (limit <= 0) {
            throw new IllegalArgumentException("limit 값이 0보다 커야 합니다.");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset 값이 음수가 될 수 없습니다.");
        }

        // 전체 개수 계산 쿼리
        String countSql = """
                SELECT COUNT(*) FROM schedule s
                 WHERE s.created_at = ? OR s.updated_at = ? OR s.deleted_at = ?
            """;
        Long totalCount = jdbcTemplate.queryForObject(countSql, new Object[]{date, date, date}, Long.class);

        if (totalCount == 0 || offset >= totalCount) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(offset / limit, limit), totalCount);
        }

        // 데이터 조회 쿼리
        String sql = """
                SELECT s.*, 
                       m.id AS member_id, m.user_id AS user_id, m.password AS member_password, 
                       m.name AS member_name, m.email AS member_email, m.updated_at AS member_updated_at
                  FROM schedule s
                  LEFT JOIN member m ON s.member_id = m.id
                 WHERE s.created_at = ? OR s.updated_at = ? OR s.deleted_at = ?
                 LIMIT ? OFFSET ?
            """;

        List<Schedule> schedules = jdbcTemplate.query(
                sql, new Object[]{date, date, date, limit, offset}, scheduleRowMapper());

        PageRequest pageRequest = PageRequest.of(offset / limit, limit);
        return new PageImpl<>(schedules, pageRequest, totalCount);
    }


    public Page<Schedule> findSchedulesByMemberId(Long memberId, SearchDto searchDto) {
        if (memberId == null) {
            throw new IllegalArgumentException("해당 회원 아이디가 존재하지 않습니다.");
        }

        int limit = searchDto.getLimit();
        int offset = searchDto.getOffset();

        if (limit <= 0) {
            throw new IllegalArgumentException("limit 값이 0보다 커야 합니다.");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset 값이 음수가 될 수 없습니다.");
        }

        // 전체 일정 개수 쿼리
        String countSql = "SELECT COUNT(*) FROM schedule WHERE member_id = ?";
        Long totalCount = jdbcTemplate.queryForObject(countSql, new Object[]{memberId}, Long.class);

        // 빈 페이지 반환 조건
        if (totalCount == 0 || offset >= totalCount) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(offset / limit, limit), totalCount);
        }

        // 데이터 조회 쿼리
        String sql = """
                SELECT s.*, 
                       m.id AS member_id, m.user_id AS user_id, m.password AS member_password, 
                       m.name AS member_name, m.email AS member_email, m.updated_at AS member_updated_at
                  FROM schedule s
                  LEFT JOIN member m ON s.member_id = m.id
                 WHERE m.id = ?
                 LIMIT ? OFFSET ?
            """;

        List<Schedule> schedules = jdbcTemplate.query(sql, new Object[]{memberId, limit, offset}, scheduleRowMapper());

        PageRequest pageRequest = PageRequest.of(offset / limit, limit);
        return new PageImpl<>(schedules, pageRequest, totalCount);
    }

    public Schedule findSingleScheduleByMemberId(Long memberId) {
        String sql = """
                    SELECT s.*, 
                           m.id AS member_id, m.user_id AS user_id, m.password AS member_password, 
                           m.name AS member_name, m.email AS member_email, m.updated_at AS member_updated_at
                      FROM schedule s
                      LEFT JOIN member m ON s.member_id = m.id
                     WHERE m.id = ?
                     LIMIT 1
                """;
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{memberId}, scheduleRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private RowMapper<Schedule> scheduleRowMapper() {
        return (rs, rowNum) -> {
            // Member 정보 매핑
            Member member = null;
            if (rs.getObject("member_id") != null) {

                member = Member.builder()
                        .id(rs.getLong("member_id"))
                        .userId(rs.getString("user_id"))
                        .password(rs.getString("member_password"))
                        .name(rs.getString("member_name"))
                        .email(rs.getString("member_email"))
                        .updatedAt(rs.getDate("updated_at"))
                        .build();
            }

            return Schedule.builder()
                    .id(rs.getLong("id"))
                    .title(rs.getString("title"))
                    .author(rs.getString("author"))
                    .password(rs.getString("password"))
                    .description(rs.getString("description"))
                    .createdAt(rs.getDate("created_at"))
                    .updatedAt(rs.getDate("updated_at"))
                    .deletedAt(rs.getDate("deleted_at"))
                    .member(member)
                    .build();
        };
    }

    private RowMapper<Schedule> simpleScheduleRowMapper() {
        return (rs, rowNum) -> Schedule.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .author(rs.getString("author"))
                .password(rs.getString("password"))
                .description(rs.getString("description"))
                .createdAt(rs.getDate("created_at"))
                .updatedAt(rs.getDate("updated_at"))
                .deletedAt(rs.getDate("deleted_at"))
                .build();  // build()로 객체를 완성하고 반환
    }
}