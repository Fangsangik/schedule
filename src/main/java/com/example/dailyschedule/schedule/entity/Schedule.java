package com.example.dailyschedule.schedule.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {
    private Long id;
    private String title; //이벤트 제목
    private String author; //작성자
    private String password; //비말번호
    private String description; //상세 내용
    @JsonFormat(pattern = "yyyy:MM:dd:HH:mm")
    private LocalDateTime createdAt; //생성 날짜
    @JsonFormat(pattern = "yyyy:MM:dd:HH:mm")
    private LocalDateTime updatedAt; //수정 날짜
    @JsonFormat(pattern = "yyyy:MM:dd:HH:mm")
    private LocalDateTime deletedAt; //삭제 날짜
}
