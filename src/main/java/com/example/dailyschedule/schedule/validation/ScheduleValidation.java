package com.example.dailyschedule.schedule.validation;

import com.example.dailyschedule.error.CustomException;
import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.member.repository.MemberRepository;
import com.example.dailyschedule.schedule.dto.CombinedScheduleDto;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.dto.SearchDto;
import com.example.dailyschedule.schedule.entity.Schedule;
import com.example.dailyschedule.schedule.repository.ScheduleRepositoryImpl;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

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
    public void validateUpdateDateAndAuthor(LocalDateTime updatedAt, String author, SearchDto searchDto) {
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

    // 업데이트를 위한 유효성 검증 및 스케줄 준비 메서드
    public Schedule validateAndPrepareUpdatedSchedule(CombinedScheduleDto combinedScheduleDto, Schedule schedule, SearchDto searchDto) {
        if (searchDto == null) {
            searchDto = new SearchDto(); // 기본 페이지와 페이지 크기를 가진 인스턴스 생성
        }

        // updatedAt 날짜로 스케줄 조회 및 유효성 검사
        Page<Schedule> schedulesByDate = scheduleRepository.findByDate(combinedScheduleDto.getScheduleDto().getUpdatedAt(), searchDto);
        if (schedulesByDate.isEmpty()) {
            throw new CustomException(NOT_FOUND);
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

    // Schedule 유효성 검증 및 업데이트를 위한 스케줄 준비 메서드 (오버로딩)
    public Schedule validateAndPrepareUpdatedSchedule(CombinedScheduleDto combinedScheduleDto, Schedule schedule) {
        return validateAndPrepareUpdatedSchedule(combinedScheduleDto, schedule, null);
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
