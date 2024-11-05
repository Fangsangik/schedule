package com.example.dailyschedule.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class SingleDateScheduleDto {
    private Long id;
    private String title;
    private String author;
    private Date selectedDate;
}
