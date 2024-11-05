package com.example.dailyschedule.member.controller;

import com.example.dailyschedule.error.CustomException;
import com.example.dailyschedule.error.type.ErrorCode;
import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
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

    //아이디 조회
    @GetMapping("/{memberId}")
    public ResponseEntity<?> findByMemberId(@PathVariable("memberId") Long memberId) {
        try {
            MemberDto findMember = memberService.findById(memberId);
            return ResponseEntity.ok(findMember);
        } catch (IllegalArgumentException e) {
            log.error("해당 회원을 찾을 수 없습니다. {}", e.getMessage());
            throw new CustomException(ID_NOT_FOUND);
        }
    }

    //userId 조회
    @GetMapping("/userId")
    public ResponseEntity<?> findByUserId(@RequestParam("userId") String userId) {
        try {
            MemberDto findMember = memberService.findByUserId(userId);
            return ResponseEntity.ok(findMember);
        } catch (IllegalArgumentException e) {
            log.error("해당 회원을 찾을 수 없습니다. {}", e.getMessage());
            throw new CustomException(ErrorCode.NOT_FOUND);
        }
    }

    //생성
    @PostMapping("/")
    public ResponseEntity<?> createMember(@RequestBody MemberDto memberDto) {
        try {
            MemberDto createMember = memberService.createMember(memberDto);
            return ResponseEntity.ok(createMember);
        } catch (IllegalArgumentException e) {
            log.error("회원 생성하는데 실패했습니다. {}", e.getMessage());
            throw new CustomException(CREATION_FAILED);
        }
    }

    //UPDATE
    @PutMapping("/{memberId}")
    public ResponseEntity<?> updateMember(@PathVariable("memberId") Long memberId, @RequestBody MemberDto memberDto) {
        try {
            MemberDto updatedMember = memberService.updateMember(memberId, memberDto);
            return ResponseEntity.ok(updatedMember);
        } catch (IllegalArgumentException e) {
            log.error("회원 update 하는데 실패했습니다. {}", e.getMessage());
            throw new CustomException(UPDATE_FAILED);
        }
    }

    //삭제
    @DeleteMapping("/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable("memberId") Long memberId, MemberDto memberDto) {
        try {
            MemberDto deleteMember = memberService.deleteMember(memberId, memberDto);
            return ResponseEntity.ok(deleteMember);
        } catch (IllegalArgumentException e) {
            log.error("회원을 삭제하는데 실패했습니다. {}", e.getMessage());
            throw new CustomException(DELETE_FAILED);
        }
    }
}
