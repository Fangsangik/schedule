package com.example.dailyschedule.domain;

import com.example.dailyschedule.dto.ScheduleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private LocalDateTime createdAt; //생성 날짜
    private LocalDateTime updatedAt; //수정 날짜
    private LocalDateTime deletedAt; //삭제 날짜

    //password 로직 검증
    public Schedule updateByScheduleDto(ScheduleDto scheduleDto) {
        if (!scheduleDto.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        this.title = scheduleDto.getTitle();
        this.author = scheduleDto.getAuthor();
        this.updatedAt = LocalDateTime.now();

        return this;
    }

    public Schedule deleteByScheduleById(ScheduleDto scheduleDto) {
        if (!scheduleDto.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (!scheduleDto.getId().equals(id)) {
            throw new IllegalArgumentException("아이디가 일치하지 않습니다.");
        }

        this.deletedAt = LocalDateTime.now();

        return this;
    }
}
