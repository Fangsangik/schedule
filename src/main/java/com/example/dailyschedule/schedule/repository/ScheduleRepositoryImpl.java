package com.example.dailyschedule.schedule.repository;

import com.example.dailyschedule.schedule.entity.Schedule;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ScheduleRepositoryImpl {

    private final JdbcTemplate jdbcTemplate;

    public ScheduleRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    //스케줄 생성
    public Schedule createSchedule(Schedule schedule) {


        String sql = "INSERT INTO schedule (author, title, created_at, password, description, updated_at, deleted_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                schedule.getAuthor(),
                schedule.getTitle(),
                schedule.getCreatedAt(),
                schedule.getPassword(),
                schedule.getDescription(),
                schedule.getUpdatedAt(),
                schedule.getDeletedAt()
        );
        //최근 추가된 ID 조회
        Long generatedId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return Schedule.builder()
                .id(generatedId)
                .author(schedule.getAuthor())
                .title(schedule.getTitle())
                .password(schedule.getPassword())
                .description(schedule.getDescription())
                .createdAt(schedule.getCreatedAt())  // DTO에서 설정된 createdAt 사용
                .updatedAt(schedule.getUpdatedAt())  // DTO에서 설정된 updatedAt 사용 없으면 현재 시간 반영
                .deletedAt(schedule.getDeletedAt())
                .build();
    }

    //스케줄 update
    public Schedule updateSchedule(Schedule schedule) {
        if (schedule.getId() == null) {
            throw new IllegalArgumentException("해당 id가 존재하지 않습니다.");
        }

        String sql = "UPDATE schedule SET title = ?, author = ?, description = ?, updated_at = ?, password = ?, created_at = ?, deleted_at = ? WHERE id = ?";

        int updatedRows = jdbcTemplate.update(sql,
                schedule.getTitle(),
                schedule.getAuthor(),
                schedule.getDescription(),
                schedule.getUpdatedAt(),
                schedule.getPassword(),
                schedule.getCreatedAt(),
                schedule.getDeletedAt(),
                schedule.getId());  // 마지막 파라미터로 id 추가
        if (updatedRows == 0) {
            throw new IllegalArgumentException("업데이트 실패");
        }
        return schedule;
    }


    public Schedule findScheduleById(Long id) {
        // 필요한 모든 필드를 조회하도록 쿼리 작성
        String sql = "SELECT * FROM schedule WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, scheduleRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;  // 조회 결과가 없을 경우 null 반환
        }
    }

    //스케줄 삭제
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

    //수정일과 작성자 명으로 스케줄 조회
    public List<Schedule> findSchedulesByUpdatedDateAndAuthor(Date updatedAt, String author) {
        StringBuilder sql = new StringBuilder("SELECT * FROM schedule WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (updatedAt != null) {
            sql.append(" AND updated_at = ?");
            params.add(updatedAt);
        }

        if (author != null) {
            sql.append(" AND author = ?");
            params.add(author);
        }

        sql.append(" ORDER BY updated_at DESC");

        return jdbcTemplate.query(sql.toString(), params.toArray(), scheduleRowMapper());
    }

    //내림차순 조회
    public List<Schedule> findAllOrderByUpdatedDateDesc() {
        String sql = "SELECT * FROM schedule ORDER BY updated_at DESC";
        return jdbcTemplate.query(sql, scheduleRowMapper());
    }

    //해당 날짜 조회
    public List<Schedule> findByDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("해당 날짜가 없습니다.");
        }

        String sql = "SELECT * FROM schedule WHERE created_at = ? OR updated_at = ? OR deleted_at = ?";

        // query 메서드를 사용하여 다수의 결과를 리스트로 반환
        return jdbcTemplate.query(sql, new Object[]{date, date, date}, scheduleRowMapper());
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
        return jdbcTemplate.queryForObject(sql, new Object[]{date, id}, scheduleRowMapper());
    }

    //RowMapper
    private RowMapper<Schedule> scheduleRowMapper() {
        return (rs, rowNum) -> Schedule.builder()
                .id(rs.getLong("id"))
                .author(rs.getString("author"))
                .title(rs.getString("title"))
                .password(rs.getString("password"))
                .description(rs.getString("description"))
                .createdAt(rs.getDate("created_at"))
                .updatedAt(rs.getDate("updated_at"))
                .deletedAt(rs.getDate("deleted_at"))
                .build();
    }

}
