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
//id로 선택 날짜 title author 조회
public class SingleDateScheduleDto {
    private Long id;
    private String title;
    private String author;
    private Date selectedDate;
}