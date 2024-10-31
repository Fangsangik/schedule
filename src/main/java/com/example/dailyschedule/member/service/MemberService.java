package com.example.dailyschedule.member.service;

import com.example.dailyschedule.member.converter.MemberConverter;
import com.example.dailyschedule.member.validation.MemberValidation;
import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Member memberId = memberValidation.validateExistId(id);
        return memberConverter.toDto(memberId);
    }

    @Transactional(readOnly = true)
    public MemberDto findByUserId(String userId) {
        Member memberUserId = memberValidation.findByUserId(userId);
        return memberConverter.toDto(memberUserId);
    }

    @Transactional(readOnly = true)
    public List<MemberDto> findAll() {
        List<Member> members = memberRepository.findAll();
        return members.stream().map(memberConverter::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MemberDto findByUsername(String username) {
        Member name = memberValidation.findByName(username);
        return memberConverter.toDto(name);
    }

    @Transactional
    public MemberDto createMember(MemberDto memberDto) {
        memberValidation.validateDuplicateUserId(memberDto.getUserId());
        Member member = memberRepository.createMember(memberConverter.toEntity(memberDto));
        return memberConverter.toDto(member);
    }

    @Transactional
    public MemberDto updateMember(MemberDto memberDto) {
        Member existMember = memberValidation.validateExistId(memberDto.getId());
        memberValidation.validatePassword(existMember, memberDto);
        Member updateMember = memberRepository.updateMember(existMember);
        return memberConverter.toDto(updateMember);
    }

    @Transactional
    public MemberDto deleteMember(MemberDto memberDto) {
        Member member = memberValidation.validateExistId(memberDto.getId());
        memberValidation.validatePassword(member, memberDto);
        memberRepository.deleteMember(member.getId());
        return memberConverter.toDto(member);
    }
}
