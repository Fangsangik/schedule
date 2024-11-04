package com.example.dailyschedule.member;

import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.member.repository.MemberRepository;
import com.example.dailyschedule.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

//DB Test 한 것
@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void createMemberTest() {
        memberRepository.deleteMember();
        MemberDto memberDto = MemberDto.builder()
                .id(1L)
                .userId("uniqueUser123")
                .password("password")
                .name("Test User")
                .email("uniqueuser123@test.com")
                .build();

        MemberDto createdMember = memberService.createMember(memberDto);

        assertNotNull(createdMember);
        assertEquals("uniqueUser123", createdMember.getUserId());
    }


    @Test
    @Rollback(false)
    void findById(){
        memberRepository.deleteMember();
        MemberDto memberDto = MemberDto.builder()
                .id(1L)
                .name("Test User")
                .userId("uniqueUser123")
                .password("password")
                .email("uniqueuser123@test.com")
                .build();

        MemberDto createdMember = memberService.createMember(memberDto);

        assertNotNull(createdMember);
        assertEquals("uniqueUser123", createdMember.getUserId());

        // 생성된 멤버 조회
        MemberDto findId = memberService.findById(createdMember.getId());
        assertThat(findId).isNotNull();
        assertThat(findId.getId()).isEqualTo(createdMember.getId());
    }

    @Test
    void findByUserId() {
        memberRepository.deleteMember();
        MemberDto memberDto = MemberDto.builder()
                .id(1L)
                .name("Test User")
                .userId("uniqueUser123")
                .password("password")
                .email("uniqueuser123@test.com")
                .build();

        MemberDto createdMember = memberService.createMember(memberDto);

        assertNotNull(createdMember);
        assertEquals("uniqueUser123", createdMember.getUserId());

        Member userId = memberRepository.findByUserId(createdMember.getUserId());
        assertThat(userId).isNotNull();
        assertThat(userId.getUserId()).isEqualTo(createdMember.getUserId());
    }

    @Test
    void findAll() {
        memberRepository.deleteMember();
        MemberDto memberDto1 = MemberDto.builder()
                .name("Test User")
                .userId("uniqueUser123")
                .password("password")
                .email("uniqueuser123@test.com")
                .build();

        MemberDto memberDto2 = MemberDto.builder()
                .name("Test User1")
                .userId("uniqueUser2")
                .password("password1")
                .email("uniqueuser123@test.com")
                .build();

        MemberDto createdMember1 = memberService.createMember(memberDto1);
        MemberDto createdMember2 = memberService.createMember(memberDto2);

        List<MemberDto> findAllMembers = memberService.findAll();
        assertThat(findAllMembers).isNotNull();
        assertThat(findAllMembers.size()).isEqualTo(2);
        assertThat(findAllMembers.get(0).getId()).isEqualTo(createdMember1.getId());
        assertThat(findAllMembers.get(1).getId()).isEqualTo(createdMember2.getId());

    }

    @Test
    void findByUsername() {
        memberRepository.deleteMember();
        MemberDto memberDto1 = MemberDto.builder()
                .name("Test User")
                .userId("uniqueUser123")
                .password("password")
                .email("uniqueuser123@test.com")
                .build();

        MemberDto createdMember1 = memberService.createMember(memberDto1);
        MemberDto memberName = memberService.findByUsername(createdMember1.getName());
        assertThat(createdMember1).isNotNull();
        assertThat(createdMember1.getUserId()).isEqualTo("uniqueUser123");
        assertThat(memberName.getName()).isEqualTo("Test User");
    }

    @Test
    void updateMember() {
        memberRepository.deleteMember();

        // 테스트할 초기 멤버 생성
        MemberDto memberDto1 = MemberDto.builder()
                .id(1L)
                .name("Test User")
                .userId("uniqueUser123")
                .password("password")
                .email("uniqueuser123@test.com")
                .build();

        // 멤버 생성
        MemberDto createdMember1 = memberService.createMember(memberDto1);

        // 업데이트할 멤버 정보 준비
        MemberDto memberDto2 = MemberDto.builder()
                .id(createdMember1.getId()) // id 설정
                .userId("uniqueUser122")    // userId만 수정
                .password(createdMember1.getPassword())
                .name(createdMember1.getName())
                .email(createdMember1.getEmail())
                .build();

        // 업데이트 수행
        MemberDto updatedMember = memberService.updateMember(createdMember1.getId(), memberDto2);

        // 검증 단계
        assertThat(updatedMember).isNotNull();
        assertThat(updatedMember.getId()).isEqualTo(createdMember1.getId());
        assertThat(updatedMember.getName()).isEqualTo(createdMember1.getName());
        assertThat(updatedMember.getPassword()).isEqualTo(createdMember1.getPassword());
        assertThat(updatedMember.getEmail()).isEqualTo(createdMember1.getEmail());
        assertThat(updatedMember.getUserId()).isEqualTo("uniqueUser122");
    }

    @Test
    void deleteMember() {
        // 테스트할 초기 멤버 생성
        MemberDto memberDto1 = MemberDto.builder()
                .id(1L)
                .name("Test User")
                .userId("uniqueUser123")
                .password("password")
                .email("uniqueuser123@test.com")
                .build();

        // 멤버 생성
        MemberDto createdMember1 = memberService.createMember(memberDto1);
        MemberDto deleteMember = memberService.deleteMember(createdMember1.getId(), createdMember1);
        // 삭제된 멤버가 더 이상 존재하지 않음을 확인
        assertThat(deleteMember).isNotNull();
        assertThat(memberRepository.findById(createdMember1.getId())).isNull(); // 존재하지 않아야 함
    }
}
