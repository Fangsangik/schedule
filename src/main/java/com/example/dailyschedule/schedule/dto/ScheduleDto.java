package com.example.dailyschedule.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ScheduleDto {
    private Long id;
    private String title; //이벤트 제목
    private String author; //작성자
    private String password; //비말번호
    private String description; //상세 내용
    @JsonFormat(pattern = "yyyy:MM:dd")
    private LocalDateTime createdAt; //생성 날짜
    @JsonFormat(pattern = "yyyy:MM:dd")
    private LocalDateTime updatedAt; //수정 날짜
    @JsonFormat(pattern = "yyyy:MM:dd")
    private LocalDateTime deletedAt; //삭제 날짜
}
