package com.example.dailyschedule.schedule.validation;
import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.member.repository.MemberRepository;
import com.example.dailyschedule.schedule.dto.CombinedScheduleDto;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.entity.Schedule;
import com.example.dailyschedule.schedule.repository.ScheduleRepositoryImpl;

import java.time.LocalDateTime;
import java.util.List;

public class ScheduleValidation {

    private final ScheduleRepositoryImpl scheduleRepository;
    private final MemberRepository memberRepository;

    public ScheduleValidation(ScheduleRepositoryImpl scheduleRepository, MemberRepository memberRepository) {
        this.scheduleRepository = scheduleRepository;
        this.memberRepository = memberRepository;
    }

    // 비밀번호 검증 메서드
    public void validatePassword(String inputPassword, Schedule schedule) {
        if (!schedule.getPassword().equals(inputPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    // ID 검증 메서드
    public void validateId(Long inputId, Schedule schedule) {
        if (!inputId.equals(schedule.getId())) {
            throw new IllegalArgumentException("아이디가 일치하지 않습니다.");
        }
    }

    // ID 중복 검증 메서드
    public Schedule validationOfDuplicateId(Long id) {
        Schedule existSchedule = scheduleRepository.findScheduleById(id);
        if (existSchedule != null) {
            throw new IllegalArgumentException("해당 아이디가 존재합니다.");
        }
        return existSchedule;
    }

    // 존재하는 ID 검증 메서드
    public Schedule validateExistId(Long id) {
        Schedule existingSchedule = scheduleRepository.findScheduleById(id);
        if (existingSchedule == null) {
            throw new IllegalArgumentException("ID 값이 없습니다.");
        }
        return existingSchedule;
    }

    // updatedAt과 author를 검증하는 메서드
    public void validateUpdateDateAndAuthor(LocalDateTime updatedAt, String author) {
        List<Schedule> schedules = scheduleRepository.findSchedulesByUpdatedDateAndAuthor(updatedAt, author);

        if (schedules.isEmpty()) {
            throw new IllegalArgumentException("해당 날짜와 작성자에 해당하는 일정이 없습니다.");
        }
    }

    // 스케줄 삭제를 위한 ID와 비밀번호 검증 메서드
    public void deleteByScheduleById(Long id, String password, Schedule schedule) {
        validatePassword(password, schedule);
        validateId(id, schedule);
    }

    // 업데이트를 위한 유효성 검증 및 스케줄 준비 메서드
    public Schedule validateAndPrepareUpdatedSchedule(CombinedScheduleDto combinedScheduleDto, Schedule schedule) {
        // updatedAt 날짜로 스케줄 조회 및 유효성 검사
        List<Schedule> schedulesByDate = scheduleRepository.findByDate(combinedScheduleDto.getScheduleDto().getUpdatedAt());
        if (schedulesByDate.isEmpty()) {
            throw new IllegalArgumentException("해당 날짜에 해당하는 일정이 존재하지 않습니다.");
        }

        // ID와 비밀번호 유효성 검사
        validateExistId(combinedScheduleDto.getScheduleDto().getId());
        validatePassword(combinedScheduleDto.getScheduleDto().getPassword(), schedule);

        // 새로운 Schedule 객체 생성하여 업데이트 내용 반영
        return Schedule.builder()
                .id(schedule.getId())                        // 기존 ID 유지
                .createdAt(schedule.getCreatedAt())          // 기존 생성일 유지
                .updatedAt(LocalDateTime.now())              // 현재 시간으로 updatedAt 변경
                .author(combinedScheduleDto.getScheduleDto().getAuthor())     // DTO에서 가져온 author 값으로 변경
                .title(combinedScheduleDto.getScheduleDto().getTitle())       // DTO에서 가져온 title 값으로 변경
                .password(schedule.getPassword())            // 기존 비밀번호 유지
                .build();
    }

    //회원 아이디와 스케줄 아이디 검증
    public void validationOfFindScheduleByMemberId(ScheduleDto scheduleDto, MemberDto memberDto) {
        Member findMember = memberRepository.findById(memberDto.getId());
        Schedule findSchedule = scheduleRepository.findScheduleById(scheduleDto.getId());

        if (findMember == null) {
            throw new IllegalArgumentException("해당 사용자가 없습니다.");
        }

        if (findSchedule == null) {
            throw new IllegalArgumentException("해당 스케줄이 없습니다.");
        }
    }
}
