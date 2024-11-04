package com.example.dailyschedule.schedule.service;

import com.example.dailyschedule.member.converter.MemberConverter;
import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.member.repository.MemberRepository;
import com.example.dailyschedule.member.service.MemberService;
import com.example.dailyschedule.schedule.dto.CombinedScheduleDto;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.service.ScheduleServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
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
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
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

        schedule = ScheduleDto.builder()
                .id(1L)
                .title("test title")
                .description("test description")
                .author("test author")
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
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
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
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
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .password("TestPassword")
                .memberDto(memberDto)
                .build();

        ScheduleDto createdSchedule = scheduleService.create(memberConverter.toDto(createdMember), schedule);
        assertNotNull(createdSchedule);

        scheduleService.deleteById(createdSchedule.getId(), createdSchedule.getPassword());

        assertThrows(IllegalArgumentException.class, () -> {
            scheduleService.findById(createdSchedule.getId());
        });
    }

    @Test
    @Transactional
    void findByUpdatedDateDesc() {
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
                .title("First Test Title")
                .description("First Test Description")
                .author("Author1")
                .createdAt(LocalDateTime.of(2024, 07, 01, 00, 00))
                .updatedAt(LocalDateTime.of(2024, 07, 10, 00, 00))
                .password("password1")
                .build();

        ScheduleDto schedule2 = ScheduleDto.builder()
                .title("Second Test Title")
                .description("Second Test Description")
                .author("Author2")
                .createdAt(LocalDateTime.of(2024, 06, 01, 00, 00))
                .updatedAt(LocalDateTime.of(2024, 07, 05, 00, 00))
                .password("password2")
                .build();

        scheduleService.create(memberConverter.toDto(createdMember), schedule1);
        scheduleService.create(memberConverter.toDto(createdMember), schedule2);


        List<ScheduleDto> scheduleDtoList = scheduleService.findByUpdatedDateDesc();
        assertFalse(scheduleDtoList.isEmpty());
        assertThat(scheduleDtoList.size()).isEqualTo(2);
        assertThat(scheduleDtoList.get(0).getUpdatedAt()).isEqualTo(LocalDateTime.of(2024, 07, 10, 00, 00));
        assertThat(scheduleDtoList.get(1).getUpdatedAt()).isEqualTo(LocalDateTime.of(2024, 07, 05, 00, 00));
    }

    @Test
    void updatedDateByAuthorAndTitle() {
        memberRepository.deleteMemberAndSchedule();

        memberDto = MemberDto.builder()
                .id(1L)
                .userId("user_Id")
                .password("testPassword")
                .name("testName")
                .email("test@test.com")
                .build();

        MemberDto createdMember = memberService.createMember(memberDto);

        // 첫 번째 일정 생성
        ScheduleDto schedule1 = ScheduleDto.builder()
                .title("Original Test Title")
                .description("Original Test Description")
                .author("Original Author")
                .createdAt(LocalDateTime.of(2024, 7, 1, 0, 0))
                .updatedAt(LocalDateTime.of(2024, 7, 10, 0, 0))
                .password("password1")
                .memberDto(memberDto)
                .build();

        // 일정 생성 및 생성된 일정의 ID 확인
        ScheduleDto createdSchedule = scheduleService.create(createdMember, schedule1);

        // 업데이트할 제목과 작성자 설정
        ScheduleDto updatedScheduleInfo = ScheduleDto.builder()
                .id(createdSchedule.getId())
                .title("Updated Test Title")
                .author("Updated Author")
                .password("password1")
                .updatedAt(LocalDateTime.of(2024, 7, 10, 0, 0)) // 동일한 날짜 사용
                .build();

        CombinedScheduleDto combinedScheduleDto = CombinedScheduleDto.builder()
                .scheduleDto(updatedScheduleInfo) // 업데이트할 정보 반영
                .memberDto(createdMember)
                .build();

        // 제목과 작성자명 업데이트
        ScheduleDto updatedSchedule = scheduleService.updateTitleAndAuthor(createdSchedule.getId(), combinedScheduleDto);

        // 업데이트된 제목과 작성자를 검증
        assertThat(updatedSchedule.getTitle()).isEqualTo("Updated Test Title");
        assertThat(updatedSchedule.getAuthor()).isEqualTo("Updated Author");
    }

    @Test
    void findDateTest() {
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
                .title("Original Test Title")
                .description("Original Test Description")
                .author("Original Author")
                .createdAt(LocalDateTime.of(2024, 07, 01, 00, 00))
                .updatedAt(LocalDateTime.of(2024, 07, 10, 00, 00))
                .password("password1")
                .memberDto(memberDto)
                .build();

        ScheduleDto scheduleDto1 = scheduleService.create(memberConverter.toDto(createdMember), schedule1);


        List<ScheduleDto> createdAtDate = scheduleService.findByDate(scheduleDto1.getCreatedAt());
        assertNotNull(createdAtDate);
        assertThat(createdAtDate.get(0).getCreatedAt()).isEqualTo("2024-07-01T00:00");

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
                .title("Original Test Title")
                .description("Original Test Description")
                .author("Original Author")
                .createdAt(LocalDateTime.of(2024, 07, 01, 00, 00))
                .updatedAt(LocalDateTime.of(2024, 07, 10, 00, 00))
                .password("password1")
                .memberDto(memberDto)
                .build();

        ScheduleDto cratedSchedule = scheduleService.create(memberConverter.toDto(createdMember), schedule1);
        List<ScheduleDto> findSchedule = scheduleService.findByUpdatedDateAndAuthor(schedule1.getUpdatedAt(), cratedSchedule.getAuthor());
        assertThat(findSchedule.get(0).getUpdatedAt()).isEqualTo("2024-07-10T00:00");
        assertThat(findSchedule.get(0).getTitle()).isEqualTo("Original Test Title");
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

        // 첫 번째 테스트 스케줄 생성
        ScheduleDto schedule1 = ScheduleDto.builder()
                .title("Original Test Title 1")
                .description("Original Test Description")
                .author("Original Author")
                .createdAt(LocalDateTime.of(2024, 7, 1, 0, 0))
                .updatedAt(LocalDateTime.of(2024, 7, 10, 0, 0))
                .password("password1")
                .memberDto(memberDto)
                .build();
        ScheduleDto createdSchedule1 = scheduleService.create(memberDto, schedule1);

        // 두 번째 테스트 스케줄 생성
        ScheduleDto schedule2 = ScheduleDto.builder()
                .title("Original Test Title 2")
                .description("Original Test Description")
                .author("Original Author")
                .createdAt(LocalDateTime.of(2024, 7, 1, 0, 0))
                .updatedAt(LocalDateTime.of(2024, 10, 11, 11, 0))
                .password("password2")
                .memberDto(memberDto)
                .build();
        ScheduleDto createdSchedule2 = scheduleService.create(memberDto, schedule2);

        // 특정 멤버 ID와 스케줄 ID로 스케줄 리스트 조회
        List<ScheduleDto> schedules = scheduleService.findSchedulesByMemberId(createdMember.getId(), createdSchedule1.getId());

        // 검증
        assertThat(schedules).isNotEmpty();
        assertThat(schedules.size()).isEqualTo(2); // 두 개의 스케줄이 생성되었는지 확인
        assertThat(schedules).extracting("title").contains("Original Test Title 1", "Original Test Title 2"); // 제목 검증
    }

    @Test
    void findScheduleByMemberId() {
        // 테스트 멤버 생성
        memberDto = MemberDto.builder()
                .id(1L)
                .userId("user_Id")
                .password("testPassword")
                .name("testName")
                .email("test@test.com")
                .build();

        Member createdMember = memberRepository.createMember(memberConverter.toEntity(memberDto));

        // 테스트 스케줄 생성
        ScheduleDto schedule1 = ScheduleDto.builder()
                .title("Original Test Title")
                .description("Original Test Description")
                .author("Original Author")
                .createdAt(LocalDateTime.of(2024, 7, 1, 0, 0))
                .updatedAt(LocalDateTime.of(2024, 7, 10, 0, 0))
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
        assertThat(schedule.getUpdatedAt()).isEqualTo(LocalDateTime.of(2024, 7, 10, 0, 0));
    }

}