package com.example.dailyschedule.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UpdatedScheduleDto {
    private String title; // 할일
    private String author; // 작성자명
    private String password; // 인증용 비밀번호
}
