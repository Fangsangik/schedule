package com.example.dailyschedule.schedule.dto;

import com.example.dailyschedule.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;


@NoArgsConstructor
@AllArgsConstructor
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
    private MemberDto memberDto;
}
