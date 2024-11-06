package com.example.dailyschedule.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

//삭제 Request
@Getter
public class DeleteScheduleRequest {
    private Long id;
    @NotBlank(message = "비밀번호를 입력해 주세요")
    private String password;
}
