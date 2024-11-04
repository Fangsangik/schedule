package com.example.dailyschedule.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteScheduleRequest {
    private Long id;
    @NotBlank(message = "비밀번호를 입력해 주세요")
    private String password;
}
