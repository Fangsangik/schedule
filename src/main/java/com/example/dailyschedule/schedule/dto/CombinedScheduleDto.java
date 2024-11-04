package com.example.dailyschedule.schedule.dto;

import com.example.dailyschedule.member.dto.MemberDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CombinedScheduleDto {
    private  MemberDto memberDto;
    private ScheduleDto scheduleDto;
}
