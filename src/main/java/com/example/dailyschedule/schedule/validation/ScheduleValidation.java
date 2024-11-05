package com.example.dailyschedule.schedule.validation;
import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.member.repository.MemberRepository;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.dto.UpdateScheduleDto;
import com.example.dailyschedule.schedule.entity.Schedule;
import com.example.dailyschedule.schedule.repository.ScheduleRepositoryImpl;

import java.sql.Date;
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
    public void validateUpdateDateAndAuthor(Date updatedAt, String author) {
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

    public Schedule validateAndPrepareUpdatedSchedule(UpdateScheduleDto updateScheduleDto, Schedule existingSchedule) {
        // ID와 비밀번호 유효성 검사
        if (!existingSchedule.getPassword().equals(updateScheduleDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 새로운 Schedule 객체 생성하여 업데이트 내용 반영
        return Schedule.builder()
                .id(existingSchedule.getId()) // ID는 기존 값 유지
                .title(updateScheduleDto.getTitle()) // Title은 변경하지 않음
                .author(updateScheduleDto.getAuthor()) // 변경된 작성자명
                .password(existingSchedule.getPassword()) // 비밀번호는 기존 값 유지
                .description(existingSchedule.getDescription()) // 변경된 할일(description)
                .createdAt(existingSchedule.getCreatedAt()) // 작성일은 기존 값 유지
                .updatedAt(new Date(System.currentTimeMillis())) // 수정일을 현재 시점으로 설정
                .deletedAt(existingSchedule.getDeletedAt()) // 삭제일은 기존 값 유지
                .member(existingSchedule.getMember()) // Member는 기존 값 유지
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
