package com.example.dailyschedule.service;

import com.example.dailyschedule.error.CustomException;
import com.example.dailyschedule.member.converter.MemberConverter;
import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.member.repository.MemberRepository;
import com.example.dailyschedule.member.service.MemberService;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.dto.SearchDto;
import com.example.dailyschedule.schedule.dto.UpdatedScheduleDto;
import com.example.dailyschedule.schedule.service.ScheduleServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScheduleServiceImplTest {

    @Autowired
    private ScheduleServiceImpl scheduleService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberConverter memberConverter;

    private ScheduleDto schedule;
    private MemberDto memberDto;
    @Autowired
    private MemberService memberService;

    @Test
    void create() {

        memberDto = MemberDto.builder()
                .id(1L)
                .userId("user_Id")
                .password("testPassword")
                .name("testName")
                .email("test@test.com")
                .build();

        Member createdMember = memberRepository.createMember(memberConverter.toEntity(memberDto));

        schedule = ScheduleDto.builder()
                .id(1L)
                .title("test title")
                .description("test description")
                .author("test author")
                .createdAt(new Date(System.currentTimeMillis()))
                .updatedAt(new Date(System.currentTimeMillis()))
                .password("TestPassword")
                .memberDto(memberDto)
                .build();

        ScheduleDto createdSchedule = scheduleService.create(memberConverter.toDto(createdMember), schedule);

        assertNotNull(createdSchedule);
        assertThat(createdSchedule.getTitle()).isEqualTo(schedule.getTitle());
        assertThat(createdSchedule.getDescription()).isEqualTo(schedule.getDescription());
        assertThat(createdSchedule.getAuthor()).isEqualTo(schedule.getAuthor());
        assertThat(createdSchedule.getCreatedAt()).isEqualTo(schedule.getCreatedAt());
        assertThat(createdSchedule.getUpdatedAt()).isEqualTo(schedule.getUpdatedAt());
    }


    @Test
    @Transactional
    void findById() {
        memberRepository.deleteMemberAndSchedule();
        memberDto = MemberDto.builder()
                .id(1L)
                .userId("user_Id")
                .password("testPassword")
                .name("testName")
                .email("test@test.com")
                .build();

        Member createdMember = memberRepository.createMember(memberConverter.toEntity(memberDto));

        schedule = ScheduleDto.builder()
                .id(1L)
                .title("test title")
                .description("test description")
                .author("test author")
                .createdAt(new Date(System.currentTimeMillis()))
                .updatedAt(new Date(System.currentTimeMillis()))
                .password("TestPassword")
                .memberDto(memberDto)
                .build();

        ScheduleDto createdSchedule = scheduleService.create(memberConverter.toDto(createdMember), schedule);
        assertNotNull(createdSchedule);

        ScheduleDto findId = scheduleService.findById(createdSchedule.getId());
        assertNotNull(findId, "스케줄을 찾을 수 없습니다.");
        assertEquals(createdSchedule.getId(), findId.getId());
    }

    @Test
    @Transactional
    void update() {
        memberRepository.deleteMemberAndSchedule();
        memberDto = MemberDto.builder()
                .id(1L)
                .userId("user_Id")
                .password("testPassword")
                .name("testName")
                .email("test@test.com")
                .build();

        Member createdMember = memberRepository.createMember(memberConverter.toEntity(memberDto));
        assertNotNull(createdMember.getId(), "생성된 Member의 ID가 null이 아님을 확인합니다."); // ID가 null이 아님을 확인
        memberDto = memberConverter.toDto(createdMember); // memberDto에 생성된 ID 반영

        schedule = ScheduleDto.builder()
                .id(1L)
                .title("test title")
                .description("test description")
                .author("test author")
                .createdAt(new Date(System.currentTimeMillis()))
                .updatedAt(new Date(System.currentTimeMillis()))
                .password("TestPassword")
                .memberDto(memberDto)
                .build();

        ScheduleDto createdSchedule = scheduleService.create(memberDto, schedule);
        assertNotNull(createdSchedule);

        ScheduleDto updatedData = ScheduleDto.builder()
                .id(createdSchedule.getId())
                .title("updated title")
                .description("updated description")
                .author("updated author")
                .createdAt(schedule.getCreatedAt())
                .updatedAt(new Date(System.currentTimeMillis()))
                .password("updated password")
                .build();

        ScheduleDto updatedSchedule = scheduleService.update(memberConverter.toDto(createdMember), updatedData);
        assertNotNull(updatedSchedule);
        assertThat(updatedSchedule.getTitle()).isEqualTo("updated title");
        assertThat(updatedSchedule.getDescription()).isEqualTo("updated description");
        assertThat(updatedSchedule.getAuthor()).isEqualTo("updated author");
        assertThat(updatedSchedule.getPassword()).isEqualTo("updated password");
    }

    @Test
    @Transactional
    void deleteById() {

        memberRepository.deleteMemberAndSchedule();
        memberDto = MemberDto.builder()
                .id(1L)
                .userId("user_Id")
                .password("testPassword")
                .name("testName")
                .email("test@test.com")
                .build();
        Member createdMember = memberRepository.createMember(memberConverter.toEntity(memberDto));


        schedule = ScheduleDto.builder()
                .id(1L)
                .title("test title")
                .description("test description")
                .author("test author")
                .createdAt(new Date(System.currentTimeMillis()))
                .updatedAt(new Date(System.currentTimeMillis()))
                .password("TestPassword")
                .memberDto(memberDto)
                .build();

        ScheduleDto createdSchedule = scheduleService.create(memberConverter.toDto(createdMember), schedule);
        assertNotNull(createdSchedule);

        scheduleService.deleteById(createdSchedule.getId(), createdSchedule.getPassword());

        assertThrows(CustomException.class, () -> {
            scheduleService.findById(createdSchedule.getId());
        });
    }

    @Test
    @Transactional
    void findByUpdatedDateDesc_withPagination() {
        memberRepository.deleteMemberAndSchedule();
        memberDto = MemberDto.builder()
                .id(1L)
                .userId("user_Id")
                .password("testPassword")
                .name("testName")
                .email("test@test.com")
                .build();
        MemberDto createdMember = memberService.createMember(memberDto);

        ScheduleDto schedule1 = ScheduleDto.builder()
                .id(1L)
                .title("First Test Title")
                .description("First Test Description")
                .author("Author1")
                .createdAt(new Date(System.currentTimeMillis()))
                .updatedAt(new Date(System.currentTimeMillis()))
                .password("password1")
                .build();

        ScheduleDto schedule2 = ScheduleDto.builder()
                .id(2L)
                .title("Second Test Title")
                .description("Second Test Description")
                .author("Author2")
                .createdAt(new Date(2024, 07,10))
                .updatedAt(new Date(2024, 07, 10))
                .password("password2")
                .memberDto(createdMember)
                .build();

        scheduleService.create(createdMember, schedule1);
        scheduleService.create(createdMember, schedule2);

        // SearchDto를 통한 페이지네이션 설정
        SearchDto searchDto = SearchDto.builder()
                .page(0) // 첫 번째 페이지
                .pageSize(1) // 페이지당 1개씩
                .build();

        Page<ScheduleDto> schedulePage = scheduleService.findByUpdatedDateDesc(searchDto);

        assertFalse(schedulePage.isEmpty());
        assertThat(schedulePage.getContent().size()).isEqualTo(2); // 한 페이지에 한 개의 스케줄이 있어야 함
        assertThat(schedulePage.getTotalElements()).isEqualTo(2); // 전체 스케줄 개수
        assertThat(schedulePage.getContent().get(0).getUpdatedAt()).isEqualTo(new Date(2024, 7, 10)); // 최신 날짜 확인
    }

    @Test
    void updatedDateByAuthorAndTitle() {
        // 테스트 환경 준비: 기존 멤버와 일정 삭제
        memberRepository.deleteMemberAndSchedule();

        // 멤버 생성
        MemberDto memberDto = MemberDto.builder()
                .id(1L)
                .userId("user_Id")
                .password("testPassword")
                .name("testName")
                .email("test@test.com")
                .build();

        MemberDto createdMember = memberService.createMember(memberDto);

        // 첫 번째 일정 생성
        ScheduleDto schedule1 = ScheduleDto.builder()
                .id(1L)
                .title("Original Test Title")
                .description("Original Test Description")
                .author("Original Author")
                .createdAt(new Date(System.currentTimeMillis()))
                .updatedAt(new Date(System.currentTimeMillis()))
                .password("password1")
                .memberDto(createdMember) // memberDto 포함
                .build();

        ScheduleDto createdSchedule = scheduleService.create(createdMember, schedule1);

        // 업데이트할 정보만 포함된 UpdateScheduleDto 생성
        UpdatedScheduleDto updateScheduleInfo = UpdatedScheduleDto.builder()
                .title("Updated Test Description") // 할일(description) 업데이트
                .author("Updated Author") // 작성자명 업데이트
                .password("password1") // 비밀번호로 인증
                .build();

        // 제목과 작성자명 업데이트
        ScheduleDto updatedSchedule = scheduleService.updateTitleAndAuthor(createdSchedule.getId(), updateScheduleInfo);

        // 업데이트된 필드를 검증
        assertThat(updatedSchedule.getTitle()).isEqualTo("Updated Test Description");
        assertThat(updatedSchedule.getAuthor()).isEqualTo("Updated Author");

        // 수정일이 업데이트된 시점으로 변경되었는지 검증
        assertThat(updatedSchedule.getUpdatedAt()).isAfter(createdSchedule.getUpdatedAt());
    }


    @Test
    @Transactional
    void findByDate_withPagination() {
        memberRepository.deleteMemberAndSchedule();


        memberDto = MemberDto.builder()
                .id(1L)
                .userId("user_Id")
                .password("testPassword")
                .name("testName")
                .email("test@test.com")
                .build();

        Member createdMember = memberRepository.createMember(memberConverter.toEntity(memberDto));

        ScheduleDto schedule1 = ScheduleDto.builder()
                .id(1L)
                .title("Original Test Title")
                .description("Original Test Description")
                .author("Original Author")
                .createdAt(new Date(2024, 07,10))
                .updatedAt(new Date(2024, 07, 10))
                .password("password1")
                .memberDto(memberDto)
                .build();

        ScheduleDto scheduleDto = scheduleService.create(memberConverter.toDto(createdMember), schedule1);

        SearchDto searchDto = SearchDto.builder()
                .page(0)
                .pageSize(1)
                .build();

        Page<ScheduleDto> findDates = scheduleService.findByDate(scheduleDto.getCreatedAt(), searchDto);

        assertFalse(findDates.isEmpty());
        assertThat(findDates.getContent().size()).isEqualTo(1);
        assertThat(findDates.getTotalElements()).isEqualTo(1);
        assertThat(findDates.getContent().get(0).getCreatedAt()).isEqualTo(scheduleDto.getCreatedAt());
    }


    @Test
    void findByUpdateDateAndAuthor() {
        memberRepository.deleteMemberAndSchedule();

        memberDto = MemberDto.builder()
                .id(1L)
                .userId("user_Id")
                .password("testPassword")
                .name("testName")
                .email("test@test.com")
                .build();

        Member createdMember = memberRepository.createMember(memberConverter.toEntity(memberDto));


        ScheduleDto schedule1 = ScheduleDto.builder()
                .id(1L)
                .title("Original Test Title")
                .description("Original Test Description")
                .author("Original Author")
                .createdAt(new Date(2024, 07,10))
                .updatedAt(new Date(2024, 07, 10))
                .password("password1")
                .memberDto(memberDto)
                .build();

        SearchDto searchDto = SearchDto.builder()
                .page(0)
                .pageSize(1)
                .build();

        ScheduleDto cratedSchedule = scheduleService.create(memberConverter.toDto(createdMember), schedule1);
        Page<ScheduleDto> findSchedule = scheduleService.findByUpdatedDateAndAuthor(schedule1.getUpdatedAt(), cratedSchedule.getAuthor(), searchDto);
        assertThat(findSchedule.getContent().get(0).getUpdatedAt()).isEqualTo(new Date(2024, 7, 10));
        assertThat(findSchedule.getContent().get(0).getTitle()).isEqualTo("Original Test Title");
    }

    @Test
    void findSchedulesByMemberId() {
        memberRepository.deleteMemberAndSchedule();

        // 테스트 멤버 생성
        memberDto = MemberDto.builder()
                .id(1L)
                .userId("user_Id")
                .password("testPassword")
                .name("testName")
                .email("test@test.com")
                .build();

        Member createdMember = memberRepository.createMember(memberConverter.toEntity(memberDto));
        assertNotNull(createdMember.getId(), "생성된 Member의 ID가 null이 아님을 확인합니다."); // ID가 null이 아님을 확인
        memberDto = memberConverter.toDto(createdMember); // memberDto에 생성된 ID 반영

        // 첫 번째 테스트 스케줄 생성
        ScheduleDto schedule1 = ScheduleDto.builder()
                .id(1L)
                .title("Original Test Title 1")
                .description("Original Test Description")
                .author("Original Author")
                .createdAt(new Date(2024, 07,10))
                .updatedAt(new Date(2024, 07, 10))
                .password("password1")
                .memberDto(memberDto)
                .build();
        ScheduleDto createdSchedule1 = scheduleService.create(memberDto, schedule1);

        // 두 번째 테스트 스케줄 생성
        ScheduleDto schedule2 = ScheduleDto.builder()
                .id(2L)
                .title("Original Test Title 2")
                .description("Original Test Description")
                .author("Original Author")
                .createdAt(new Date(2024, 07,10))
                .updatedAt(new Date(2024, 07, 10))
                .password("password2")
                .memberDto(memberDto)
                .build();
        ScheduleDto createdSchedule2 = scheduleService.create(memberDto, schedule2);

        SearchDto searchDto = SearchDto.builder()
                .page(0)
                .pageSize(1)
                .build();

        // 특정 멤버 ID와 스케줄 ID로 스케줄 리스트 조회
        Page<ScheduleDto> schedules = scheduleService.findSchedulesByMemberId(searchDto, createdMember.getId(), createdSchedule1.getId());

        // 검증
        assertThat(schedules).isNotEmpty();
        assertThat(schedules.getContent().size()).isEqualTo(2); // 두 개의 스케줄이 생성되었는지 확인
        assertThat(schedules.getTotalElements()).isEqualTo(2);
        assertThat(schedules).extracting("title").contains("Original Test Title 1", "Original Test Title 2"); // 제목 검증
    }

    @Test
    void findScheduleByMemberId() {
        memberRepository.deleteMemberAndSchedule();

        // 테스트 멤버 생성
        memberDto = MemberDto.builder()
                .id(1L)
                .userId("user_Id")
                .password("testPassword")
                .name("testName")
                .email("test@test.com")
                .build();

        Member createdMember = memberRepository.createMember(memberConverter.toEntity(memberDto));
        assertNotNull(createdMember.getId(), "생성된 Member의 ID가 null이 아님을 확인합니다."); // ID가 null이 아님을 확인
        memberDto = memberConverter.toDto(createdMember); // memberDto에 생성된 ID 반영

        // 테스트 스케줄 생성
        ScheduleDto schedule1 = ScheduleDto.builder()
                .id(1L)
                .title("Original Test Title")
                .description("Original Test Description")
                .author("Original Author")
                .createdAt(new Date(2024, 07,10))
                .updatedAt(new Date(2024, 07, 10))
                .password("password1")
                .memberDto(memberDto)
                .build();
        ScheduleDto createdSchedule1 = scheduleService.create(memberDto, schedule1);

        // 특정 멤버 ID와 스케줄 ID로 단일 스케줄 조회
        ScheduleDto schedule = scheduleService.findScheduleByMemberId(createdMember.getId(), createdSchedule1.getId());

        // 검증
        assertThat(schedule).isNotNull();
        assertThat(schedule.getTitle()).isEqualTo("Original Test Title");
        assertThat(schedule.getDescription()).isEqualTo("Original Test Description");
        assertThat(schedule.getAuthor()).isEqualTo("Original Author");
        assertThat(schedule.getUpdatedAt()).isEqualTo(new Date(2024, 7, 10));
    }

}