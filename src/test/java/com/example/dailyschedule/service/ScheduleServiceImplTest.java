package com.example.dailyschedule.service;

import com.example.dailyschedule.member.converter.MemberConverter;
import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.member.repository.MemberRepository;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.dto.SearchDto;
import com.example.dailyschedule.schedule.repository.ScheduleRepositoryImpl;
import com.example.dailyschedule.schedule.service.ScheduleServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

        scheduleService.deleteById(createdSchedule);

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
                .title("First Test Title")
                .description("First Test Description")
                .author("Author1")
                .createdAt(LocalDateTime.of(2024, 07, 01, 00, 00))
                .updatedAt(LocalDateTime.of(2024, 07, 10, 00, 00))
                .password("password1")
                .memberDto(memberDto)
                .build();

        ScheduleDto schedule2 = ScheduleDto.builder()
                .title("Second Test Title")
                .description("Second Test Description")
                .author("Author2")
                .createdAt(LocalDateTime.of(2024, 06, 01, 00, 00))
                .updatedAt(LocalDateTime.of(2024, 07, 05, 00, 00))
                .password("password2")
                .memberDto(memberDto)
                .build();

        scheduleService.create(memberConverter.toDto(createdMember), schedule1);
        scheduleService.create(memberConverter.toDto(createdMember), schedule2);

        SearchDto searchDto = SearchDto.builder()
                .page(1)
                .pageSize(10)
                .recordSize(10)
                .build();

        Page<ScheduleDto> scheduleDtoList = scheduleService.findByUpdatedDateDesc(searchDto);
        assertFalse(scheduleDtoList.isEmpty());
        assertThat(scheduleDtoList.getTotalElements()).isEqualTo(12);
        assertThat(scheduleDtoList.getContent().get(0).getUpdatedAt()).isEqualTo(LocalDateTime.of(2024, 7, 10, 0, 0));
        assertThat(scheduleDtoList.getContent().get(1).getUpdatedAt()).isEqualTo(LocalDateTime.of(2024, 7, 5, 0, 0));
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

        Member createdMember = memberRepository.createMember(memberConverter.toEntity(memberDto));


        // 첫 번째 일정 생성
        ScheduleDto schedule1 = ScheduleDto.builder()
                .title("Original Test Title")
                .description("Original Test Description")
                .author("Original Author")
                .createdAt(LocalDateTime.of(2024, 07, 01, 00, 00))
                .updatedAt(LocalDateTime.of(2024, 07, 10, 00, 00))
                .password("password1")
                .memberDto(memberDto)
                .build();

        // 일정 생성 및 생성된 일정의 ID 확인
        ScheduleDto createdSchedule = scheduleService.create(memberConverter.toDto(createdMember), schedule1);

        // 업데이트할 제목과 작성자 정보 설정 (생성된 ID 사용)
        ScheduleDto schedule2 = ScheduleDto.builder()
                .id(createdSchedule.getId())               // 생성된 일정의 ID를 사용
                .title("Updated Test Title")               // 새로운 제목
                .description(schedule1.getDescription())   // 기존 설명 유지
                .author("Updated Author")                  // 새로운 작성자
                .createdAt(schedule1.getCreatedAt())
                .updatedAt(LocalDateTime.of(2024, 07, 15, 00, 00))  // 새로운 업데이트 날짜
                .password("password1")                     // 동일한 비밀번호
                .build();

        // 제목과 작성자명 업데이트
        ScheduleDto updatedSchedule = scheduleService.updateTitleAndAuthor(memberConverter.toDto(createdMember), schedule2);

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


        ScheduleDto createdAtDate = scheduleService.findByDate(scheduleDto1.getCreatedAt());
        assertNotNull(createdAtDate);
        assertThat(createdAtDate.getCreatedAt()).isEqualTo("2024-07-01T00:00");

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
        ScheduleDto findSchedule = scheduleService.findByUpdatedDateAndAuthor(schedule1.getUpdatedAt(), cratedSchedule.getAuthor());
        assertThat(findSchedule.getUpdatedAt()).isEqualTo("2024-07-10T00:00");
        assertThat(findSchedule.getTitle()).isEqualTo("Original Test Title");
    }
}