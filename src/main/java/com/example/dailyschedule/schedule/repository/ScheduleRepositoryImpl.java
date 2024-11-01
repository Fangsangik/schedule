package com.example.dailyschedule.schedule.repository;

import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.member.repository.MemberRepository;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.dto.SearchDto;
import com.example.dailyschedule.schedule.entity.Schedule;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ScheduleRepositoryImpl {

    private final JdbcTemplate jdbcTemplate;
    private final MemberRepository memberRepository;

    public ScheduleRepositoryImpl(JdbcTemplate jdbcTemplate, MemberRepository memberRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.memberRepository = memberRepository;
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
                schedule.getUpdatedAt() != null ? schedule.getUpdatedAt() : LocalDateTime.now(),
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
                .updatedAt(schedule.getUpdatedAt() != null ? schedule.getUpdatedAt() : LocalDateTime.now())
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
        // 필요한 모든 필드를 조회하도록 쿼리 작성
        String sql = "SELECT * FROM schedule WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, scheduleRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;  // 조회 결과가 없을 경우 null 반환
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
    @Transactional
    public void deleteAll() {
        String sql = "delete from schedule";

        int deleteAll = jdbcTemplate.update(sql);
        if (deleteAll < 0) {
            throw new IllegalArgumentException("전체 삭제에 실패했습니다.");
        }
    }


    public Schedule findByUpdatedDateAndAuthor(LocalDateTime updatedAt, String author) {
        if (updatedAt == null && author == null) {
            throw new IllegalArgumentException("해당 이름으로 수정된 날짜를 찾을 수 없습니다.");
        }

        String sql = "SELECT * FROM schedule WHERE (updated_at = ? OR ? IS NULL) or (author = ? OR ? IS NULL) ORDER BY updated_at DESC";

        return jdbcTemplate.queryForObject(sql, new Object[]{updatedAt, updatedAt
                , author, author}, scheduleRowMapper());
    }


    public Page<Schedule> findAllOrderByUpdatedDateDesc(SearchDto searchDto) {
        String sql = "SELECT * FROM schedule ORDER BY updated_at DESC LIMIT ? OFFSET ?";

        int offset = searchDto.getOffset();

        List<Schedule> schedules = jdbcTemplate.query(sql, new Object[]{searchDto.getRecordSize(), offset}, scheduleRowMapper());



        return new PageImpl<>(schedules, PageRequest.of(searchDto.getPage(), searchDto.getRecordSize()), schedules.size());
    }


    public Schedule findByDate(LocalDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("해당 날짜가 없습니다.");
        }
        String sql = "SELECT * FROM schedule " +
                "WHERE created_at = ? OR updated_at = ? OR deleted_at = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{date, date, date}, scheduleRowMapper());
    }


    public Page<Schedule> findSchedulesByMemberId(SearchDto searchDto, Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("해당 회원 아이디가 존재하지 않습니다.");
        }

        int offset = searchDto.getOffset();

        String sql = "select * from schedule s left join member m on s.member_id = m.id where m.id = ? LIMIT ? OFFSET ?";

        List<Schedule> schedules = jdbcTemplate.query(sql, new Object[]{searchDto.getRecordSize(), offset, memberId}, scheduleRowMapper());

        return new PageImpl<>(schedules, PageRequest.of(searchDto.getPage(), searchDto.getRecordSize()), schedules.size());
    }

    public Schedule findSingleScheduleByMemberId(Long memberId) {
        String sql = "SELECT * FROM schedule s LEFT JOIN member m ON s.member_id = m.id WHERE m.id = ? LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{memberId}, scheduleRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    private RowMapper<Schedule> scheduleRowMapper() {
        return (rs, rowNum) -> {
            Long memberId = rs.getLong("member_id");

            // 필요한 경우 memberId로 Member 객체 생성 또는 조회
            Member member = Member.builder().id(memberId).build();

            return Schedule.builder()
                    .id(rs.getLong("id"))
                    .author(rs.getString("author"))
                    .title(rs.getString("title"))
                    .password(rs.getString("password"))
                    .description(rs.getString("description"))
                    .createdAt(rs.getObject("created_at", LocalDateTime.class))
                    .updatedAt(rs.getObject("updated_at", LocalDateTime.class))
                    .deletedAt(rs.getObject("deleted_at", LocalDateTime.class))
                    .member(member)  // member_id를 설정한 Member 객체 설정
                    .build();
        };
    }
}
