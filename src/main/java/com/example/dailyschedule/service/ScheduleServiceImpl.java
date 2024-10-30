package com.example.dailyschedule.service;

import com.example.dailyschedule.converter.ScheduleConverter;
import com.example.dailyschedule.domain.Schedule;
import com.example.dailyschedule.dto.ScheduleDto;
import com.example.dailyschedule.repository.ScheduleRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl {

    private final ScheduleRepositoryImpl scheduleRepositoryImpl;
    private final ScheduleConverter scheduleConverter;

    //생성자 주입
    public ScheduleServiceImpl(ScheduleRepositoryImpl scheduleRepositoryImpl, ScheduleConverter scheduleConverter) {
        this.scheduleRepositoryImpl = scheduleRepositoryImpl;
        this.scheduleConverter = scheduleConverter;
    }


    @Transactional
    public ScheduleDto create(ScheduleDto scheduleDto) {
        Schedule saveSchedule = scheduleRepositoryImpl.createSchedule(scheduleConverter.toEntity(scheduleDto));
        return scheduleConverter.toDto(saveSchedule);
    }


    @Transactional(readOnly = true)
    public ScheduleDto findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id는 null 이면 안됩니다");
        }

        // ScheduleRepository에서 id로 Schedule 객체를 찾고, ScheduleDto로 변환하여 반환
        Schedule schedule = scheduleRepositoryImpl.findScheduleById(id);

        // 조회 결과가 없을 경우 null 반환
        if (schedule == null) {
            throw new IllegalArgumentException("id null");
        }

        return scheduleConverter.toDto(schedule);
    }

    @Transactional(readOnly = true)
    public ScheduleDto findByUpdatedAt_author (LocalDate updatedAt, String author) {
        if (updatedAt == null && author == null) {
            throw new IllegalArgumentException("해당 이름으로 수정된 날짜를 찾을 수 없습니다.");
        }

        Schedule byUpdateAndAuthor = scheduleRepositoryImpl.findByUpdateAndAuthor(updatedAt, author);

        if (byUpdateAndAuthor == null) {
            throw new IllegalArgumentException("updated date and author is null");
        }

        return scheduleConverter.toDto(byUpdateAndAuthor);
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

    //Lv2
    @Transactional
    public ScheduleDto updatedDateByAuthorAndTitle(ScheduleDto scheduleDto) {
        if (scheduleDto.getUpdatedAt() == null || scheduleDto.getAuthor() == null || scheduleDto.getTitle() == null) {
            throw new IllegalArgumentException("해당 값이 null 이면 안됩니다.");
        }

        Schedule existSchedule = scheduleRepositoryImpl.findScheduleById(scheduleDto.getId());
        Schedule updatedSchedule = existSchedule.updateByScheduleDto(scheduleDto);
        Schedule updated = scheduleRepositoryImpl.updateSchedule(updatedSchedule);

        return scheduleConverter.toDto(updated);
    }

    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id는 null 이면 안됩니다");
        }

        scheduleRepositoryImpl.deleteScheduleById(id);
    }
}
