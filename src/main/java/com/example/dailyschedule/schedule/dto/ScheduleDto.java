package com.example.dailyschedule.schedule.dto;

import com.example.dailyschedule.member.dto.MemberDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.sql.Date;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleDto {
    @NotNull(message = "ID는 필수 입니다.")
    private Long id;

    @NotBlank(message = "제목은 필수 입니다.")
    @Size(max = 100, message = "제목은 최대 100자까지 입력 가능합니다.")
    private String title; //이벤트 제목

    @NotBlank(message = "작성자는 필수 입니다.")
    private String author; //작성자

    @NotBlank(message = "비밀번호는 필수 입니다.")
    private String password; //비말번호

    @Size(max = 500, message = "상세 내용은 500자 이내로 작성 가능 합니다.")
    private String description; //상세 내용
    private Date createdAt; //생성 날짜
    private Date updatedAt; //수정 날짜
    private Date deletedAt; //삭제 날짜
    //@Valid
    private MemberDto memberDto;
}
