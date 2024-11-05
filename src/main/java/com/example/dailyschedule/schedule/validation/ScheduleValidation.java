package com.example.dailyschedule.schedule.validation;

import com.example.dailyschedule.error.CustomException;
import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.member.repository.MemberRepository;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.dto.SearchDto;
import com.example.dailyschedule.schedule.dto.UpdatedScheduleDto;
import com.example.dailyschedule.schedule.entity.Schedule;
import com.example.dailyschedule.schedule.repository.ScheduleRepositoryImpl;
import org.springframework.data.domain.Page;

import java.sql.Date;

import static com.example.dailyschedule.error.type.ErrorCode.*;

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
            throw new CustomException(PASSWORD_INCORRECT);

        }
    }

    // ID 검증 메서드
    public void validateId(Long inputId, Schedule schedule) {
        if (!inputId.equals(schedule.getId())) {
            throw new CustomException(ID_INCORRECT);
        }
    }

    // ID 중복 검증 메서드
    public Schedule validationOfDuplicateId(Long id) {
        Schedule existSchedule = scheduleRepository.findScheduleById(id);
        if (existSchedule != null) {
            throw new CustomException(ID_EXIST);
        }
        return existSchedule;
    }

    // 존재하는 ID 검증 메서드
    public Schedule validateExistId(Long id) {
        Schedule existingSchedule = scheduleRepository.findScheduleById(id);
        if (existingSchedule == null) {
            throw new CustomException(ID_NOT_FOUND);
        }
        return existingSchedule;
    }

    // updatedAt과 author를 검증하는 메서드
    public void validateUpdateDateAndAuthor(Date updatedAt, String author, SearchDto searchDto) {
        Page<Schedule> schedules = scheduleRepository.findSchedulesByUpdatedDateAndAuthor(updatedAt, author, searchDto);

        if (schedules.isEmpty()) {
            throw new CustomException(NOT_FOUND);
        }
    }

    // 스케줄 삭제를 위한 ID와 비밀번호 검증 메서드
    public void deleteByScheduleById(Long id, String password, Schedule schedule) {
        validatePassword(password, schedule);
        validateId(id, schedule);
    }

    public Schedule validateAndPrepareUpdatedSchedule(UpdatedScheduleDto updatedScheduleDto, Schedule existingSchedule) {
        // ID와 비밀번호 유효성 검사
        if (!existingSchedule.getPassword().equals(updatedScheduleDto.getPassword())) {
            throw new CustomException(PASSWORD_INCORRECT);
        }

        // 새로운 Schedule 객체 생성하여 업데이트 내용 반영
        return Schedule.builder()
                .id(existingSchedule.getId()) // ID는 기존 값 유지
                .title(updatedScheduleDto.getTitle()) // Title은 변경하지 않음
                .author(updatedScheduleDto.getAuthor()) // 변경된 작성자명
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
            throw new CustomException(NOT_FOUND);
        }

        if (findSchedule == null) {
            throw new CustomException(NOT_FOUND);
        }
    }
}
