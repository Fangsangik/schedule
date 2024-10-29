package com.example.dailyschedule.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private LocalDate createdAt; //생성 날짜
    private LocalDate updatedAt; //수정 날짜
    private LocalDate deletedAt; //삭제 날짜
}
