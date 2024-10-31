package com.example.dailyschedule.schedule.service;

import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.repository.MemberRepository;
import com.example.dailyschedule.schedule.converter.ScheduleConverter;
import com.example.dailyschedule.schedule.entity.Schedule;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.repository.ScheduleRepositoryImpl;
import com.example.dailyschedule.schedule.validation.ScheduleValidation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl {

    private final ScheduleRepositoryImpl scheduleRepositoryImpl;
    private final ScheduleConverter scheduleConverter;
    private final ScheduleValidation scheduleValidation;
    private final MemberRepository memberRepository;

    //생성자 주입
    public ScheduleServiceImpl(ScheduleRepositoryImpl scheduleRepositoryImpl, ScheduleConverter scheduleConverter, MemberRepository memberRepository) {
        this.scheduleRepositoryImpl = scheduleRepositoryImpl;
        this.scheduleConverter = scheduleConverter;
        this.scheduleValidation = new ScheduleValidation(scheduleRepositoryImpl, memberRepository);
        this.memberRepository = memberRepository;
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
    public ScheduleDto findByUpdatedDateAndAuthor (LocalDateTime updatedAt, String author) {
        Schedule schedule = scheduleRepositoryImpl.findByUpdatedDateAndAuthor(updatedAt, author);
        scheduleValidation.validateUpdateDateAndAuthor(updatedAt, author, schedule);
        return scheduleConverter.toDto(schedule);
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> findByUpdatedDateDesc() {
        List<Schedule> byUpdatedDateByDesc = scheduleRepositoryImpl.findAllOrderByUpdatedDateDesc();

        List<ScheduleDto> scheduleDtos = new ArrayList<>();
        for (Schedule schedule : byUpdatedDateByDesc) {
            ScheduleDto scheduleDto = ScheduleDto.builder()
                    .id(schedule.getId())
                    .author(schedule.getAuthor())
                    .title(schedule.getTitle())
                    .description(schedule.getDescription())
                    .createdAt(schedule.getCreatedAt())
                    .updatedAt(schedule.getUpdatedAt())
                    .deletedAt(schedule.getDeletedAt())
                    .build();
            scheduleDtos.add(scheduleDto);
        }
        return scheduleDtos;
    }

    @Transactional
    public ScheduleDto update(ScheduleDto scheduleDto) {
        Schedule updateSchedule = scheduleRepositoryImpl.updateSchedule(scheduleConverter.toEntity(scheduleDto));
        return scheduleConverter.toDto(updateSchedule);
    }

    @Transactional(readOnly = true)
    public ScheduleDto findByDate(LocalDateTime date) {
        Schedule findDate = scheduleRepositoryImpl.findByDate(date);

        if (findDate == null) {
            throw new IllegalArgumentException("해당 날짜에 대한 값이 존재하지 않습니다.");
        }

        return scheduleConverter.toDto(findDate);
    }

    @Transactional
    public void deleteById(ScheduleDto scheduleDto) {
        Schedule existSchedule = scheduleRepositoryImpl.findScheduleById(scheduleDto.getId());
        scheduleValidation.deleteByScheduleById(scheduleDto, existSchedule);
        scheduleRepositoryImpl.deleteScheduleById(existSchedule.getId());
    }

    //Lv2
    @Transactional
    public ScheduleDto updateTitleAndAuthor(ScheduleDto scheduleDto) {
        Schedule existingSchedule = scheduleRepositoryImpl.findScheduleById(scheduleDto.getId());
        Schedule updatedScheduleDto = scheduleValidation.updateTitleAndAuthor(scheduleDto, existingSchedule);
        scheduleRepositoryImpl.updateSchedule(updatedScheduleDto);
        return scheduleConverter.toDto(updatedScheduleDto);
    }

    //Lv3
    @Transactional(readOnly = true)
    public List<ScheduleDto> findSchedulesByMemberId(Long memberId, Long scheduleId) {
        // 검증: 해당 회원과 스케줄이 존재하는지 확인
        scheduleValidation.validationOfFindScheduleByMemberId(
                ScheduleDto.builder().id(scheduleId).build(),
                MemberDto.builder().id(memberId).build()
        );

        List<Schedule> schedules = scheduleRepositoryImpl.findSchedulesByMemberId(memberId);
        if (schedules.isEmpty()) {
            throw new IllegalArgumentException("해당 회원의 아이디에 할당된 스케줄이 없습니다.");
        }

        return schedules.stream()
                .map(scheduleConverter::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScheduleDto findScheduleByMemberId(Long memberId, Long scheduleId) {
        // 검증: 해당 회원과 스케줄이 존재하는지 확인
        scheduleValidation.validationOfFindScheduleByMemberId(
                ScheduleDto.builder().id(scheduleId).build(),
                MemberDto.builder().id(memberId).build()
        );

        // 검증이 통과하면 조회 진행
        Schedule schedule = scheduleRepositoryImpl.findSingleScheduleByMemberId(memberId);
        if (schedule == null) {
            throw new IllegalArgumentException("해당 회원의 아이디에 할당된 스케줄이 없습니다.");
        }

        return scheduleConverter.toDto(schedule);
    }

}
