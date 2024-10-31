package com.example.dailyschedule.service;

import com.example.dailyschedule.schedule.converter.ScheduleConverter;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.repository.ScheduleRepositoryImpl;
import com.example.dailyschedule.schedule.service.ScheduleServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
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
    private ScheduleRepositoryImpl scheduleRepository;

    private ScheduleDto schedule;

    @Test
    void create() {
        schedule = ScheduleDto.builder()
                .id(1L)
                .title("test title")
                .description("test description")
                .author("test author")
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .password("TestPassword")
                .build();

        ScheduleDto createdSchedule = scheduleService.create(schedule);
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
        schedule = ScheduleDto.builder()
                .id(1L)
                .title("test title")
                .description("test description")
                .author("test author")
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .password("TestPassword")
                .build();

        ScheduleDto createdSchedule = scheduleService.create(schedule);
        assertNotNull(createdSchedule);

        ScheduleDto findId = scheduleService.findById(createdSchedule.getId());
        assertNotNull(findId, "스케줄을 찾을 수 없습니다.");
        assertEquals(createdSchedule.getId(), findId.getId());
    }

    @Test
    @Transactional
    void update() {
        schedule = ScheduleDto.builder()
                .id(1L)
                .title("test title")
                .description("test description")
                .author("test author")
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .password("TestPassword")
                .build();

        ScheduleDto createdSchedule = scheduleService.create(schedule);
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

        ScheduleDto updatedSchedule = scheduleService.update(updatedData);
        assertNotNull(updatedSchedule);
        assertThat(updatedSchedule.getTitle()).isEqualTo("updated title");
        assertThat(updatedSchedule.getDescription()).isEqualTo("updated description");
        assertThat(updatedSchedule.getAuthor()).isEqualTo("updated author");
        assertThat(updatedSchedule.getPassword()).isEqualTo("updated password");
    }

    @Test
    @Transactional
    void deleteById() {
        schedule = ScheduleDto.builder()
                .id(1L)
                .title("test title")
                .description("test description")
                .author("test author")
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .password("TestPassword")
                .build();

        ScheduleDto createdSchedule = scheduleService.create(schedule);
        assertNotNull(createdSchedule);

        scheduleService.deleteById(createdSchedule);

        assertThrows(IllegalArgumentException.class, () -> {
            scheduleService.findById(createdSchedule.getId());
        });
    }

    @Test
    void findByUpdatedDateDesc() {
        scheduleRepository.deleteAll();

        ScheduleDto schedule1 = ScheduleDto.builder()
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

        scheduleService.create(schedule1);
        scheduleService.create(schedule2);

        List<ScheduleDto> scheduleDtoList = scheduleService.findByUpdatedDateDesc();
        assertFalse(scheduleDtoList.isEmpty());
        assertThat(scheduleDtoList.size()).isEqualTo(2);
        assertThat(scheduleDtoList.get(0).getUpdatedAt()).isEqualTo(LocalDateTime.of(2024, 07, 10, 00, 00));
        assertThat(scheduleDtoList.get(1).getUpdatedAt()).isEqualTo(LocalDateTime.of(2024, 07, 05, 00, 00));
    }

    @Test
    void updatedDateByAuthorAndTitle() {
        // 기존 데이터 삭제
        scheduleRepository.deleteAll();

        // 첫 번째 일정 생성
        ScheduleDto schedule1 = ScheduleDto.builder()
                .title("Original Test Title")
                .description("Original Test Description")
                .author("Original Author")
                .createdAt(LocalDateTime.of(2024, 07, 01, 00, 00))
                .updatedAt(LocalDateTime.of(2024, 07, 10, 00, 00))
                .password("password1")
                .build();

        // 일정 생성 및 생성된 일정의 ID 확인
        ScheduleDto createdSchedule = scheduleService.create(schedule1);

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
        ScheduleDto updatedSchedule = scheduleService.updateTitleAndAuthor(schedule2);

        // 업데이트된 제목과 작성자를 검증
        assertThat(updatedSchedule.getTitle()).isEqualTo("Updated Test Title");
        assertThat(updatedSchedule.getAuthor()).isEqualTo("Updated Author");
    }


    @Test
    void findDateTest() {
        scheduleRepository.deleteAll();

        ScheduleDto schedule1 = ScheduleDto.builder()
                .title("Original Test Title")
                .description("Original Test Description")
                .author("Original Author")
                .createdAt(LocalDateTime.of(2024, 07, 01, 00, 00))
                .updatedAt(LocalDateTime.of(2024, 07, 10, 00, 00))
                .password("password1")
                .build();

        ScheduleDto scheduleDto1 = scheduleService.create(schedule1);


        ScheduleDto createdAtDate = scheduleService.findByDate(scheduleDto1.getCreatedAt());
        assertNotNull(createdAtDate);
        assertThat(createdAtDate.getCreatedAt()).isEqualTo("2024-07-01T00:00");

    }

    @Test
    void findByUpdateDateAndAuthor() {
        scheduleRepository.deleteAll();

        ScheduleDto schedule1 = ScheduleDto.builder()
                .title("Original Test Title")
                .description("Original Test Description")
                .author("Original Author")
                .createdAt(LocalDateTime.of(2024, 07, 01, 00, 00))
                .updatedAt(LocalDateTime.of(2024, 07, 10, 00, 00))
                .password("password1")
                .build();

        ScheduleDto cratedSchedule = scheduleService.create(schedule1);
        ScheduleDto findSchedule = scheduleService.findByUpdatedDateAndAuthor(schedule1.getUpdatedAt(), cratedSchedule.getAuthor());
        assertThat(findSchedule.getUpdatedAt()).isEqualTo("2024-07-10T00:00");
        assertThat(findSchedule.getTitle()).isEqualTo("Original Test Title");
    }
}