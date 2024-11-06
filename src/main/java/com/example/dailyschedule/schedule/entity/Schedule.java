package com.example.dailyschedule.schedule.entity;

import com.example.dailyschedule.member.entity.Member;
import lombok.*;

import java.sql.Date;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
//스케줄 엔티티
public class Schedule {
    private Long id;
    private String title; //이벤트 제목
    private String author; //작성자
    private String password; //비말번호
    private String description; //상세 내용
    private Date createdAt; //생성 날짜
    private Date updatedAt; //수정 날짜
    private Date deletedAt; //삭제 날짜
    private Member member;
}
