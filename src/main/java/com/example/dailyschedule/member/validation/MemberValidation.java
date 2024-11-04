package com.example.dailyschedule.member.validation;

import com.example.dailyschedule.error.CustomException;
import com.example.dailyschedule.error.type.ErrorCode;
import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.member.repository.MemberRepository;


public class MemberValidation {

    private final MemberRepository memberRepository;

    public MemberValidation(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // userId 중복 검사
    public void validateDuplicateUserId(String userId) {
        Member existingMember = memberRepository.findByUserId(userId);
        if (existingMember != null) {
            throw new CustomException(ErrorCode.USER_ID_EXIST);
        }
    }

    // ID 존재 여부 검사
    public Member validateExistId(Long id) {
        Member existingMember = memberRepository.findById(id);
        if (existingMember == null) {
            throw new CustomException(ErrorCode.ID_NOT_FOUND);
        }
        return existingMember;
    }

    public Member findMemberId(Long id) {
        Member findMember = memberRepository.findById(id);
        if (findMember == null) {
            throw new CustomException(ErrorCode.ID_NOT_FOUND);
        }
        return findMember;
    }

    // 회원 조회 (userId 기준)
    public Member findByUserId(String userId) {
        Member member = memberRepository.findByUserId(userId);
        if (member == null) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }
        return member;
    }

    // 비밀번호 유효성 검사
    public void validatePassword(Member member, String password) {
        if (!member.getPassword().equals(password)) {
            throw new CustomException(ErrorCode.PASSWORD_INCORRECT);
        }
    }

    // 이름 기준으로 회원 조회
    public Member findByName(String name) {
        Member member = memberRepository.findByName(name);
        if (member == null) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }
        return member;
    }
}
