package com.example.dailyschedule.schedule.validation;

import com.example.dailyschedule.schedule.dto.UpdatedDtoSchedule;
import com.example.dailyschedule.schedule.entity.Schedule;
import com.example.dailyschedule.schedule.repository.ScheduleRepositoryImpl;

import java.sql.Date;
import java.util.List;

public class ScheduleValidation {

    private final ScheduleRepositoryImpl scheduleRepository;

    public ScheduleValidation(ScheduleRepositoryImpl scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
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

    // 업데이트를 위한 유효성 검증 및 스케줄 준비 메서드
    public Schedule validateAndPrepareUpdatedSchedule(UpdatedDtoSchedule updatedDtoSchedule, Schedule existingSchedule) {
        // updatedAt 날짜로 스케줄 조회 및 유효성 검사
        if (!existingSchedule.getPassword().equals(updatedDtoSchedule.getPassword())) {
                   throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 새로운 Schedule 객체 생성하여 업데이트 내용 반영
        return Schedule.builder()
                .id(existingSchedule.getId())                        // 기존 ID 유지
                .createdAt(existingSchedule.getCreatedAt())          // 기존 생성일 유지
                .updatedAt(new Date(System.currentTimeMillis()))              // 현재 시간으로 updatedAt 변경
                .author(updatedDtoSchedule.getAuthor())     // DTO에서 가져온 author 값으로 변경
                .title(updatedDtoSchedule.getTitle())       // DTO에서 가져온 title 값으로 변경
                .password(existingSchedule.getPassword())            // 기존 비밀번호 유지
                .build();
    }
}



