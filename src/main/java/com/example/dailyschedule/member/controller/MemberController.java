package com.example.dailyschedule.member.controller;

import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
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
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
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
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        }
    }

    //update
    @PutMapping("/{memberId}")
    public ResponseEntity<?> updateMember(@PathVariable("memberId") Long memberId, @RequestBody MemberDto memberDto) {
        try {
            MemberDto updatedMember = memberService.updateMember(memberId, memberDto);
            return ResponseEntity.ok(updatedMember);
        } catch (IllegalArgumentException e) {
            log.error("회원 update 하는데 실패했습니다. {}" , e.getMessage());
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        }
    }

    //삭제
    @DeleteMapping("/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable("memberId") Long memberId, @RequestBody String password) {
        try {
            MemberDto deleteMember = memberService.deleteMember(memberId, password);
            return ResponseEntity.ok(deleteMember);
        } catch (IllegalArgumentException e) {
            log.error("회원을 삭제하는데 실패했습니다. {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
