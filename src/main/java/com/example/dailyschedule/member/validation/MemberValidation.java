package com.example.dailyschedule.member.validation;

import com.example.dailyschedule.error.CustomException;
import com.example.dailyschedule.error.type.ErrorCode;
import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.member.repository.MemberRepository;

import static com.example.dailyschedule.error.type.ErrorCode.*;

public class MemberValidation {

    private final MemberRepository memberRepository;


    public MemberValidation(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // userId 중복 검사
    public void validateDuplicateUserId(String userId) {
        Member existingMember = memberRepository.findByUserId(userId);
        if (existingMember != null) {
            throw new CustomException(USER_ID_EXIST);
        }
    }

    public Member validateExistId(Long id) {
        Member existingMember = memberRepository.findById(id);
        if (existingMember == null) {
            throw new CustomException(ID_EXIST);
        }

        return existingMember;
    }

    public Member findByUserId(String userId) {
        Member member = memberRepository.findByUserId(userId);
        if (member == null) {
            throw new CustomException(ID_NOT_FOUND);
        }
        return member;
    }

    public void validatePassword(Member member, String inputPassword) {
        if (!member.getPassword().equals(inputPassword)) {
            throw new CustomException(PASSWORD_INCORRECT);
        }
    }

    public Member findByName(String name) {
        Member memberName = memberRepository.findByName(name);
        if (memberName == null) {
            throw new CustomException(NOT_FOUND);
        }

        return memberName;
    }
}