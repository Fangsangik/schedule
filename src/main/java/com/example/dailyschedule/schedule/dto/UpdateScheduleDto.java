package com.example.dailyschedule.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UpdateScheduleDto {
    @NotBlank(message = "할일은 필수 항목입니다.")
    private String title; // 할일

    @NotBlank(message = "작성자명은 필수 항목입니다.")
    private String author; // 작성자명

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    private String password; // 인증용 비밀번호
}
