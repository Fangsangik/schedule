package com.example.dailyschedule.schedule.dto;

import lombok.Builder;
import lombok.Getter;

import java.sql.Date;

@Getter
@Builder
public class ScheduleDto {
    private Long id;
    private String title; //이벤트 제목
    private String author; //작성자
    private String password; //비말번호
    private String description; //상세 내용
    private Date createdAt; //생성 날짜
    private Date updatedAt; //수정 날짜
    private Date deletedAt; //삭제 날짜
}
