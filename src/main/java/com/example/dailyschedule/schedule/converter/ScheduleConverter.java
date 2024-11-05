package com.example.dailyschedule.schedule.converter;

import com.example.dailyschedule.member.converter.MemberConverter;
import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.schedule.entity.Schedule;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import org.springframework.stereotype.Component;

@Component
public class ScheduleConverter {

    private final MemberConverter memberConverter;

    public ScheduleConverter(MemberConverter memberConverter) {
        this.memberConverter = memberConverter;
    }

    // Member가 포함된 Schedule 생성
    public Schedule toEntityIncludeMember(ScheduleDto scheduleDto, Member member) {
        if (member == null) {
            throw new IllegalArgumentException("회원이 존재하지 않습니다."); // member가 null일 때 예외 처리
        }

        return Schedule.builder()
                .id(scheduleDto.getId())
                .author(scheduleDto.getAuthor())
                .title(scheduleDto.getTitle())
                .createdAt(scheduleDto.getCreatedAt())
                .deletedAt(scheduleDto.getDeletedAt())
                .description(scheduleDto.getDescription())
                .updatedAt(scheduleDto.getUpdatedAt())
                .password(scheduleDto.getPassword())
                .member(member) // Member 포함
                .build();
    }

    public Schedule toEntityWithoutMember(ScheduleDto scheduleDto) {
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
                .memberDto(memberConverter.toDto(schedule.getMember()))
                .build();
    }
}
