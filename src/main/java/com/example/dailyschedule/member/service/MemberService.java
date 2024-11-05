package com.example.dailyschedule.member.service;

import com.example.dailyschedule.member.converter.MemberConverter;
import com.example.dailyschedule.member.validation.MemberValidation;
import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberConverter memberConverter;
    private final MemberValidation memberValidation;

    public MemberService(MemberRepository memberRepository, MemberConverter memberConverter) {
        this.memberRepository = memberRepository;
        this.memberConverter = memberConverter;
        this.memberValidation = new MemberValidation(memberRepository);
    }

    @Transactional(readOnly = true)
    public MemberDto findById(Long id) {
        Member member = memberValidation.validateExistId(id);
        return memberConverter.toDto(member);
    }

    @Transactional(readOnly = true)
    public MemberDto findByUserId(String userId) {
        Member member = memberValidation.findByUserId(userId);
        return memberConverter.toDto(member);
    }

    @Transactional(readOnly = true)
    public List<MemberDto> findAll() {
        List<Member> members = memberRepository.findAll();
        return members.stream().map(memberConverter::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MemberDto findByUsername(String username) {
        Member member = memberValidation.findByName(username);
        return memberConverter.toDto(member);
    }

    @Transactional
    public MemberDto createMember(MemberDto memberDto) {
        memberValidation.validateDuplicateUserId(memberDto.getUserId());
        Member member = memberRepository.createMember(memberConverter.toEntity(memberDto));
        return memberConverter.toDto(member);
    }

    @Transactional
    public MemberDto updateMember(Long memberId, MemberDto memberDto) {
        Member existMember = memberValidation.validateExistId(memberId);
        memberValidation.validatePassword(existMember, memberDto.getPassword());

        existMember = existMember.toBuilder()
                .userId(memberDto.getUserId())
                .password(memberDto.getPassword())
                .email(memberDto.getEmail())
                .name(memberDto.getName())
                .updatedAt(new Date(System.currentTimeMillis()))
                .build();

        Member updatedMember = memberRepository.updateMember(existMember);
        return memberConverter.toDto(updatedMember);
    }

    @Transactional
    public MemberDto deleteMember(Long memberId, String password) {
        Member member = memberValidation.validateExistId(memberId);
        memberValidation.validatePassword(member, password);
        memberRepository.deleteMember(member.getId());
        return memberConverter.toDto(member);
    }
}
