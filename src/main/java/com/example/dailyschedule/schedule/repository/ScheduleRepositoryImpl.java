package com.example.dailyschedule.repository;

import com.example.dailyschedule.domain.Schedule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ScheduleRepositoryImpl {

    private final JdbcTemplate jdbcTemplate;

    public ScheduleRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


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
                .updatedAt(schedule.getUpdatedAt() != null ? schedule.getUpdatedAt() : LocalDateTime.now())  // DTO에서 설정된 updatedAt 사용 없으면 현재 시간 반영
                .deletedAt(schedule.getDeletedAt())
                .build();
    }

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
        String sql = "SELECT id, author, title, password, description, createdAt, updatedAt, deletedAt FROM schedule WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{id}, scheduleRowMapper());
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


    public Schedule findAllByUpdatedDateAndAuthor(LocalDateTime updatedAt, String author) {
        if (updatedAt == null && author == null) {
            throw new IllegalArgumentException("해당 이름으로 수정된 날짜를 찾을 수 없습니다.");
        }

        String sql = "SELECT * FROM schedule WHERE (updated_at = ? OR ? IS NULL) or (author = ? OR ? IS NULL) ORDER BY updated_at DESC";

        return jdbcTemplate.queryForObject(sql, new Object[]{updatedAt, updatedAt
                , author, author}, scheduleRowMapper());
    }


    public List<Schedule> findAllOrderByUpdatedDateDesc() {
        String sql = "SELECT * FROM schedule ORDER BY updated_at DESC";
        return jdbcTemplate.query(sql, scheduleRowMapper());
    }


    public Schedule findByDate(LocalDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("해당 날짜가 없습니다.");
        }
        String sql = "SELECT * FROM schedule " +
                "WHERE created_at = ? OR updated_at = ? OR deleted_at = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{date, date, date}, scheduleRowMapper());
    }


    //Schdeule Maopper
    private RowMapper<Schedule> scheduleRowMapper() {
        return (rs, rowNum) -> Schedule.builder()
                .id(rs.getLong("id"))
                .author(rs.getString("author"))
                .title(rs.getString("title"))
                .password(rs.getString("password"))
                .description(rs.getString("description"))
                .createdAt(rs.getObject("createdAt", LocalDateTime.class))
                .updatedAt(rs.getObject("updatedAt", LocalDateTime.class))
                .deletedAt(rs.getObject("deletedAt", LocalDateTime.class))
                .build();
    }
}
