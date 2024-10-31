package com.example.dailyschedule.member.entity;

import com.example.dailyschedule.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    private Long id;
    private String userId;
    private String password;
    private String name;
    private String email;
    private LocalDateTime updatedAt;
}
