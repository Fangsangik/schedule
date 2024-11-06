package com.example.dailyschedule.member.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;

//회원 엔티티
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Member {
    private Long id;
    private String userId;
    private String password;
    private String name;
    private String email;
    private Date updatedAt;
}