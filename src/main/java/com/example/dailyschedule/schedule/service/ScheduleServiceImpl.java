package com.example.dailyschedule.schedule.service;

import com.example.dailyschedule.schedule.converter.ScheduleConverter;
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

    @Transactional
    public ScheduleDto create(ScheduleDto scheduleDto) {
        scheduleValidation.validationOfDuplicateId(scheduleDto.getId());
        Schedule saveSchedule = scheduleRepositoryImpl.createSchedule(scheduleConverter.toEntity(scheduleDto));
        return scheduleConverter.toDto(saveSchedule);
    }

    @Transactional(readOnly = true)
    public ScheduleDto findById(Long id) {
        Schedule existId = scheduleValidation.validateExistId(id);
        return scheduleConverter.toDto(existId);
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> findByUpdatedDateAndAuthor(Date updatedAt, String author) {
        scheduleValidation.validateUpdateDateAndAuthor(updatedAt, author);

        // 모든 결과를 리스트 형태로 반환
        List<Schedule> schedules = scheduleRepositoryImpl.findSchedulesByUpdatedDateAndAuthor(updatedAt, author);
        return schedules.stream()
                .map(scheduleConverter::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> findByUpdatedDateDesc() {
        List<Schedule> byUpdatedDateByDesc = scheduleRepositoryImpl.findAllOrderByUpdatedDateDesc();
        List<ScheduleDto> scheduleDtos = new ArrayList<>();
        for (Schedule schedule : byUpdatedDateByDesc) {
            scheduleDtos.add(scheduleConverter.toDto(schedule));
        }
        return scheduleDtos;
    }

    @Transactional
    public ScheduleDto update(ScheduleDto scheduleDto) {
        Schedule updateSchedule = scheduleRepositoryImpl.updateSchedule(scheduleConverter.toEntity(scheduleDto));
        return scheduleConverter.toDto(updateSchedule);
    }

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

    @Transactional
    public ScheduleDto updateTitleAndAuthor(Long id, UpdatedDtoSchedule updatedDtoSchedule) {
        Schedule existingSchedule = scheduleRepositoryImpl.findScheduleById(id);
        Schedule updatedSchedule = scheduleValidation.validateAndPrepareUpdatedSchedule(updatedDtoSchedule, existingSchedule);
        scheduleRepositoryImpl.updateSchedule(updatedSchedule);
        return scheduleConverter.toDto(updatedSchedule);
    }

    @Transactional
    public void deleteById(Long id, String password) {
        Schedule existSchedule = scheduleRepositoryImpl.findScheduleById(id);
        scheduleValidation.deleteByScheduleById(id, password, existSchedule);
        scheduleRepositoryImpl.deleteScheduleById(existSchedule.getId());
    }
}