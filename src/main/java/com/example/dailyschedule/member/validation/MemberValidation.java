package com.example.dailyschedule.member.validation;

import com.example.dailyschedule.member.dto.MemberDto;
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
            throw new IllegalArgumentException("중복된 userId입니다.");
        }
    }

    public Member validateExistId(Long id) {
        Member existingMember = memberRepository.findById(id);
        if (existingMember == null) {
            throw new IllegalArgumentException("ID 값이 없습니다.");
        }

        return existingMember;
    }

    public Member findByUserId(String userId) {
        Member member = memberRepository.findByUserId(userId);
        if (member == null) {
            throw new IllegalArgumentException("해당 ID 회원이 존재하지 않습니다.");
        }
        return member;
    }

    public void validatePassword(Member member, String inputPassword) {
        // 데이터베이스에 저장된 비밀번호와 입력받은 비밀번호를 직접 비교
        if (!member.getPassword().equals(inputPassword)) {
            throw new IllegalArgumentException("비밀번호가 맞지 않습니다.");
        }
    }

    public Member findByName(String name) {
        Member memberName = memberRepository.findByName(name);
        if (memberName == null) {
            throw new IllegalArgumentException("해당 이름이 없습니다.");
        }

        return memberName;
    }
 }
