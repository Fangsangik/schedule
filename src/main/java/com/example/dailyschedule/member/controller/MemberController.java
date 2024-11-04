package com.example.dailyschedule.member.controller;

import com.example.dailyschedule.error.CustomException;
import com.example.dailyschedule.error.type.ErrorCode;
import com.example.dailyschedule.member.dto.DeleteMemberRequest;
import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.dailyschedule.error.type.ErrorCode.*;

@Slf4j
@RequestMapping("/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<?> findByMemberId(@PathVariable("memberId") Long memberId) {
        try {
            MemberDto findMember = memberService.findById(memberId);
            return ResponseEntity.ok(findMember);
        } catch (CustomException e) {
            log.error("해당 회원을 찾을 수 없습니다. {}", e.getMessage());
            throw new CustomException(ID_NOT_FOUND);
        }
    }

    @GetMapping("/userId")
    public ResponseEntity<?> findByUserId(@RequestParam("userId") String userId) {
        try {
            MemberDto findMember = memberService.findByUserId(userId);
            return ResponseEntity.ok(findMember);
        } catch (CustomException e) {
            log.error("해당 회원을 찾을 수 없습니다. {}", e.getMessage());
            throw new CustomException(INVALID_MEMBER_INFO);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createMember(@Valid @RequestBody MemberDto memberDto) {
        try {
            MemberDto createMember = memberService.createMember(memberDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createMember);
        } catch (CustomException e) {
            log.error("일정을 생성하는데 실패했습니다: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.CREATION_FAILED);
        }
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<?> updateMember(@PathVariable("memberId") Long memberId, @Valid @RequestBody MemberDto memberDto) {
        try {
            MemberDto updatedMember = memberService.updateMember(memberId, memberDto);
            return ResponseEntity.ok(updatedMember);
        } catch (CustomException e) {
            log.error("회원 업데이트에 실패했습니다. {}", e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable("memberId") Long memberId, @Valid @RequestBody DeleteMemberRequest deleteMemberRequest) {
        try {
            MemberDto deletedMember = memberService.deleteMember(memberId, deleteMemberRequest.getPassword());
            return ResponseEntity.ok(deletedMember);
        } catch (CustomException e) {
            log.error("회원을 삭제하는데 실패했습니다. {}", e.getMessage());
            throw new CustomException(DELETE_FAILED);
        }
    }

}
