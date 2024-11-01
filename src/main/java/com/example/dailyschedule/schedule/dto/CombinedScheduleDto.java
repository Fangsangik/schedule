package com.example.dailyschedule.schedule.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@Builder
public class CombinedScheduleDto {
    private Long id;
    private String title;
    private String author;
    private LocalDateTime updatedAt;
    private String password;
}
