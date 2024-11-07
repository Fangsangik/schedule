package com.example.dailyschedule.schedule.service;

import com.example.dailyschedule.schedule.converter.ScheduleConverter;
import com.example.dailyschedule.schedule.dto.SingleDateScheduleDto;
import com.example.dailyschedule.schedule.dto.UpdatedDtoSchedule;
import com.example.dailyschedule.schedule.entity.Schedule;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.repository.ScheduleRepositoryImpl;
import com.example.dailyschedule.schedule.validation.ScheduleValidation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl {

    private final ScheduleRepositoryImpl scheduleRepositoryImpl;
    private final ScheduleConverter scheduleConverter;
    private final ScheduleValidation scheduleValidation;

    // 생성자 주입
    public ScheduleServiceImpl(ScheduleRepositoryImpl scheduleRepositoryImpl, ScheduleConverter scheduleConverter) {
        this.scheduleRepositoryImpl = scheduleRepositoryImpl;
        this.scheduleConverter = scheduleConverter;
        this.scheduleValidation = new ScheduleValidation(scheduleRepositoryImpl);
    }

    //스케줄 생성
    @Transactional
    public ScheduleDto create(ScheduleDto scheduleDto) {
        scheduleValidation.validationOfDuplicateId(scheduleDto.getId());
        Schedule saveSchedule = scheduleRepositoryImpl.createSchedule(scheduleConverter.toEntity(scheduleDto));
        return scheduleConverter.toDto(saveSchedule);
    }

    //스케줄 Id 단건 조회
    @Transactional(readOnly = true)
    public ScheduleDto findById(Long id) {
        Schedule existId = scheduleValidation.validateExistId(id);
        return scheduleConverter.toDto(existId);
    }


    //선택 일정 조회 (선택한 일정 정보 불러오기)
    @Transactional(readOnly = true)
    public SingleDateScheduleDto findDateById(Long id, String field, Date date) {
        Schedule schedule = scheduleValidation.validateExistId(id);
        Schedule findDate = scheduleRepositoryImpl.findDateById(schedule.getId(), field, date);

        return getSingleDateScheduleDto(field, findDate);
    }

    //스케줄 update (title, author)
    @Transactional
    public ScheduleDto updateTitleAndAuthor(Long id, UpdatedDtoSchedule updatedDtoSchedule) {
        Schedule existingSchedule = scheduleRepositoryImpl.findScheduleById(id);
        Schedule updatedSchedule = scheduleValidation.validateAndPrepareUpdatedSchedule(updatedDtoSchedule, existingSchedule);
        scheduleRepositoryImpl.updateSchedule(updatedSchedule);
        return scheduleConverter.toDto(updatedSchedule);
    }

    //수정일과 작성자 명으로 스케줄 조회
    @Transactional(readOnly = true)
    public List<ScheduleDto> findSchedules(Date updatedAt, String author) {
        // 유효성 검사 수행
        scheduleValidation.validateUpdateDateAndAuthor(updatedAt, author);
        // 날짜와 작성자 기준으로 조회
        List<Schedule> schedules = scheduleRepositoryImpl.findSchedulesByUpdatedDateAndAuthor(updatedAt, author);

        return schedules.stream().
                map(scheduleConverter::toDto).
                collect(Collectors.toList());
    }

    @Transactional
    public ScheduleDto update(ScheduleDto scheduleDto) {
        Schedule updateSchedule = scheduleRepositoryImpl.updateSchedule(scheduleConverter.toEntity(scheduleDto));
        return scheduleConverter.toDto(updateSchedule);
    }

    //해당 날짜 조회
    @Transactional(readOnly = true)
    public List<ScheduleDto> findByDate(Date date) {
        List<Schedule> findDates = scheduleRepositoryImpl.findByDate(date);
        if (findDates.isEmpty()) {
            throw new IllegalArgumentException("해당 날짜에 대한 값이 존재하지 않습니다.");
        }
        List<ScheduleDto> scheduleDtoList = new ArrayList<>();
        for (Schedule findDate : findDates) {
            scheduleDtoList.add(scheduleConverter.toDto(findDate));
        }
        return scheduleDtoList;
    }

    //스케줄 삭제
    @Transactional
    public void deleteById(Long id, String password) {
        Schedule existSchedule = scheduleRepositoryImpl.findScheduleById(id);
        scheduleValidation.deleteByScheduleById(id, password, existSchedule);
        scheduleRepositoryImpl.deleteScheduleById(existSchedule.getId());
    }

    private static SingleDateScheduleDto getSingleDateScheduleDto(String field, Schedule findDate) {
        Date selectDate;

        switch (field) {
            case "created_at":
                selectDate = findDate.getCreatedAt();
                break;
            case "updated_at":
                selectDate = findDate.getUpdatedAt();
                break;
            case "deleted_at":
                selectDate = findDate.getDeletedAt();
            default:
                throw new IllegalArgumentException("유효하지 않은 필드 이름입니다.");
        }

        return new SingleDateScheduleDto(findDate.getId(), findDate.getTitle(), findDate.getAuthor(), selectDate);
    }
}