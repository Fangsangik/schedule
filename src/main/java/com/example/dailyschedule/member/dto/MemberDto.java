package com.example.dailyschedule.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Date;

//회원 Dto
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    @NotNull(message = "ID는 필수 항목입니다.")
    private Long id;

    @NotBlank(message = "사용자 ID는 필수 항목입니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    private String password;

    @NotBlank(message = "이름은 필수 항목입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    private Date updatedAt;
}