package com.example.dailyschedule.member.converter;

import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberConverter {

    public Member toEntity(MemberDto memberDto) {
        return Member.builder()
                .id(memberDto.getId())
                .name(memberDto.getName())
                .email(memberDto.getEmail())
                .userId(memberDto.getUserId())
                .password(memberDto.getPassword())
                .updatedAt(memberDto.getUpdatedAt())
                .build();
    }


    public MemberDto toDto(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .userId(member.getUserId())
                .password(member.getPassword())
                .email(member.getEmail())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
