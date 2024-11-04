package com.example.dailyschedule.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteMemberRequest {
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;
}
