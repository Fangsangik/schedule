package com.example.dailyschedule.converter;

import com.example.dailyschedule.domain.Schedule;
import com.example.dailyschedule.dto.ScheduleDto;
import org.springframework.stereotype.Component;

@Component
public class ScheduleConverter {

    public Schedule toEntity(ScheduleDto scheduleDto) {

        return Schedule.builder()
                .id(scheduleDto.getId())
                .author(scheduleDto.getAuthor())
                .title(scheduleDto.getTitle())
                .createdAt(scheduleDto.getCreatedAt())
                .deletedAt(scheduleDto.getDeletedAt())
                .description(scheduleDto.getDescription())
                .updatedAt(scheduleDto.getUpdatedAt())
                .password(scheduleDto.getPassword())
                .build();
    }

    public ScheduleDto toDto(Schedule schedule) {

        return ScheduleDto.builder()
                .id(schedule.getId())
                .author(schedule.getAuthor())
                .title(schedule.getTitle())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .deletedAt(schedule.getDeletedAt())
                .description(schedule.getDescription())
                .password(schedule.getPassword())
                .build();
    }
}
