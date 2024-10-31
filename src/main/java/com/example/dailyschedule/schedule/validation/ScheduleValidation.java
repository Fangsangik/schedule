package com.example.dailyschedule.schedule.validation;
import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.member.repository.MemberRepository;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.entity.Schedule;
import com.example.dailyschedule.schedule.repository.ScheduleRepositoryImpl;

import java.time.LocalDateTime;

public class ScheduleValidation {

    private final ScheduleRepositoryImpl scheduleRepository;
    private final MemberRepository memberRepository;

    public ScheduleValidation(ScheduleRepositoryImpl scheduleRepository, MemberRepository memberRepository) {
        this.scheduleRepository = scheduleRepository;
        this.memberRepository = memberRepository;
    }

    // 비밀번호 검증 메서드
    public void validatePassword(ScheduleDto scheduleDto, Schedule schedule) {
        if (!scheduleDto.getPassword().equals(schedule.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    // ID 검증 메서드
    public void validateId(ScheduleDto scheduleDto, Schedule schedule) {
        if (!scheduleDto.getId().equals(schedule.getId())) {
            throw new IllegalArgumentException("아이디가 일치하지 않습니다.");
        }
    }

    public Schedule validationOfDuplicateId(Long id) {
        Schedule existSchedule = scheduleRepository.findScheduleById(id);
        if (existSchedule != null) {
            throw new IllegalArgumentException("해당 아이디가 존재합니다.");
        }

        return existSchedule;
    }

    public Schedule validateExistId(Long id) {
        Schedule existingSchedule = scheduleRepository.findScheduleById(id);
        if (existingSchedule == null) {
            throw new IllegalArgumentException("ID 값이 없습니다.");
        }

        return existingSchedule;
    }

    // updatedAt과 author를 검증하는 메서드
    public void validateUpdateDateAndAuthor(LocalDateTime updatedAt, String author, Schedule schedule) {
        if (!updatedAt.equals(schedule.getUpdatedAt()) || !author.equals(schedule.getAuthor())) {
            throw new IllegalArgumentException("해당 이름이 없거나 수정된 날짜를 찾을 수 없습니다.");
        }
    }

    //password 로직 검증
    public Schedule updateTitleAndAuthor(ScheduleDto scheduleDto, Schedule schedule) {
        validateExistId(scheduleDto.getId());
        validatePassword(scheduleDto, schedule);

        // 빌더를 사용하여 새로운 Schedule 객체 생성 (필드 값 수정 반영)
        return Schedule.builder()
                .id(schedule.getId())// 기존 ID 유지
                .createdAt(schedule.getCreatedAt())
                .updatedAt(scheduleDto.getUpdatedAt()) // DTO에서 가져온 값으로 변경
                .author(scheduleDto.getAuthor())       // DTO에서 가져온 값으로 변경
                .title(scheduleDto.getTitle())         // DTO에서 가져온 값으로 변경
                .password(schedule.getPassword())      // 기존 비밀번호 유지
                .build();
    }

    //삭제 검증
    public void deleteByScheduleById(ScheduleDto scheduleDto, Schedule schedule) {
        validatePassword(scheduleDto, schedule);
        validateId(scheduleDto, schedule);
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
