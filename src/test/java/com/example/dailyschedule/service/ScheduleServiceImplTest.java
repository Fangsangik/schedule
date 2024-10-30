package com.example.dailyschedule.service;

import com.example.dailyschedule.converter.ScheduleConverter;
import com.example.dailyschedule.domain.Schedule;
import com.example.dailyschedule.dto.ScheduleDto;
import com.example.dailyschedule.repository.ScheduleRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    @Autowired
    private ScheduleConverter scheduleConverter;

    private ScheduleDto schedule;


    @Test
    void create() {
        schedule = ScheduleDto.builder()
                //.id(1L)
                .title("test title")
                .description("test description")
                .author("test author")
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .password("TestPassword")
                .build();

        // Schedule 생성
        ScheduleDto createdSchedule = scheduleService.create(schedule);
        assertNotNull(createdSchedule);

        // 필드 값 검증
        assertThat(createdSchedule.getTitle()).isEqualTo(schedule.getTitle());
        assertThat(createdSchedule.getDescription()).isEqualTo(schedule.getDescription());
        assertThat(createdSchedule.getAuthor()).isEqualTo(schedule.getAuthor());
        assertThat(createdSchedule.getCreatedAt()).isEqualTo(schedule.getCreatedAt());
        assertThat(createdSchedule.getUpdatedAt()).isEqualTo(schedule.getUpdatedAt());
    }

    @Test
    @Transactional
    void findById() {
        // 새로운 ScheduleDto 객체 생성
        schedule = ScheduleDto.builder()
                .id(1L)
                .title("test title")
                .description("test description")
                .author("test author")
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .password("TestPassword")
                .build();

        // 스케줄 생성
        ScheduleDto createSchedule = scheduleService.create(schedule);
        assertNotNull(createSchedule);  // 생성된 스케줄이 null이 아님을 확인

        // ID로 스케줄 조회
        ScheduleDto findId = scheduleService.findById(createSchedule.getId());
        assertNotNull(findId, "스케줄을 찾을 수 없습니다.");  // 조회된 스케줄이 null이 아님을 확인

        // 조회된 스케줄의 ID가 생성된 스케줄의 ID와 같은지 확인
        Assertions.assertEquals(createSchedule.getId(), findId.getId());
    }

    @Test
    @Transactional
    void update() {
        // 새로운 ScheduleDto 객체 생성
        schedule = ScheduleDto.builder()
                .id(1L)
                .title("test title")
                .description("test description")
                .author("test author")
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .password("TestPassword")
                .build();
        // Schedule 생성 및 저장
        ScheduleDto createdSchedule = scheduleService.create(schedule);
        assertNotNull(createdSchedule);

        // 업데이트할 ScheduleDto 생성
        ScheduleDto updatedData = ScheduleDto.builder()
                .id(createdSchedule.getId())
                .title("updated title")
                .description("updated description")
                .author("updated author")
                .createdAt(schedule.getCreatedAt())
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .password("updated password")
                .build();

        // 업데이트 실행
        ScheduleDto updatedSchedule = scheduleService.update(updatedData);

        // 결과 검증
        assertNotNull(updatedSchedule);
        assertThat(updatedSchedule.getTitle()).isEqualTo("updated title");
        assertThat(updatedSchedule.getDescription()).isEqualTo("updated description");
        assertThat(updatedSchedule.getAuthor()).isEqualTo("updated author");
        assertThat(updatedSchedule.getPassword()).isEqualTo("updated password");
    }

    @Test
    @Transactional
    void deleteById() {
        // 새로운 ScheduleDto 객체 생성
        schedule = ScheduleDto.builder()
                .id(1L)
                .title("test title")
                .description("test description")
                .author("test author")
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .password("TestPassword")
                .build();
        // Schedule 생성 및 저장
        ScheduleDto createdSchedule = scheduleService.create(schedule);
        assertNotNull(createdSchedule);

        // Schedule 삭제
        scheduleService.deleteById(createdSchedule);

        // 삭제 후 스케줄 찾기 시도
        assertThrows(EmptyResultDataAccessException.class, () -> {
            scheduleService.findById(createdSchedule.getId());
        });
    }

    @Test
    void findByAuthorAndUpdateDate() {
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

        ScheduleDto byUpdatedAtAuthor = scheduleService.findByUpdatedAt_author(schedule.getUpdatedAt(), createdSchedule.getAuthor());
        assertNotNull(byUpdatedAtAuthor);
        assertThat(byUpdatedAtAuthor.getTitle()).isEqualTo(createdSchedule.getTitle());
    }

    @Test
    void findByUpdatedDateDesc() {
        scheduleRepository.deleteAll();
        ScheduleDto schedule1 = ScheduleDto.builder()
                .title("First Test Title")
                .description("First Test Description")
                .author("Author1")
                .createdAt(LocalDateTime.of(2024, 07, 01, 00,00))
                .updatedAt(LocalDateTime.of(2024, 07, 10,00,00))  // 최신 날짜
                .password("password1")
                .build();

        ScheduleDto schedule2 = ScheduleDto.builder()
                .title("Second Test Title")
                .description("Second Test Description")
                .author("Author2")
                .createdAt(LocalDateTime.of(2024, 06, 01,00,00))
                .updatedAt(LocalDateTime.of(2024, 07, 05,00,00))
                .password("password2")
                .build();

        ScheduleDto schedule3 = ScheduleDto.builder()
                .title("Third Test Title")
                .description("Third Test Description")
                .author("Author3")
                .createdAt(LocalDateTime.of(2024, 05, 01,00,00))
                .updatedAt(LocalDateTime.of(2024, 07, 01,00,00))  // 가장 오래된 날짜
                .password("password3")
                .build();
        scheduleService.create(schedule1);
        scheduleService.create(schedule2);
        scheduleService.create(schedule3);

        List<ScheduleDto> scheduleDtoList = scheduleService.findByUpdatedDateDesc();

        assertFalse(scheduleDtoList.isEmpty());
        assertThat(scheduleDtoList.size()).isEqualTo(3);

        // 최신 updatedAt이 가장 첫 번째에 오는지 확인
        assertThat(scheduleDtoList.get(0).getUpdatedAt()).isEqualTo(LocalDateTime.of(2024, 07, 10, 00,00));
        assertThat(scheduleDtoList.get(1).getUpdatedAt()).isEqualTo(LocalDateTime.of(2024, 07, 05,00,00));
        assertThat(scheduleDtoList.get(2).getUpdatedAt()).isEqualTo(LocalDateTime.of(2024, 07, 01,00,00));
    }

    @Test
    void findDate() {
        scheduleRepository.deleteAll();
        ScheduleDto schedule = ScheduleDto.builder()
                .title("First Test Title")
                .description("First Test Description")
                .author("Author1")
                .createdAt(LocalDateTime.of(2024, 07, 01, 00, 00))
                .updatedAt(LocalDateTime.of(2024, 07, 10, 00, 00))  // 최신 날짜
                .password("password1")
                .build();

        ScheduleDto createSchedule = scheduleService.create(schedule);
        ScheduleDto findDate = scheduleService.findByDate(createSchedule.getCreatedAt());
        ScheduleDto updatedDate = scheduleService.findByDate(createSchedule.getUpdatedAt());
        assertThat(findDate.getCreatedAt()).isEqualTo(LocalDateTime.of(2024,07,01,00,00));
        assertThat(updatedDate.getUpdatedAt()).isEqualTo(LocalDateTime.of(2024,07,10,00,00));
    }

    @Test
    void updatedDateByAuthorAndTitle() {
        // 1. 데이터 초기화
        scheduleRepository.deleteAll();

        // 2. 초기 스케줄 생성
        ScheduleDto schedule1 = ScheduleDto.builder()
                .title("First Test Title")
                .description("First Test Description")
                .author("Author1")
                .createdAt(LocalDateTime.of(2024, 07, 01,00,00))
                .updatedAt(LocalDateTime.of(2024, 07, 10,00,00))  // 최신 날짜
                .password("password1")
                .build();

        // 스케줄 생성
        ScheduleDto createdSchedule = scheduleService.create(schedule1);

        // 3. 업데이트할 데이터 설정
        ScheduleDto updateSchedule = ScheduleDto.builder()
                .id(createdSchedule.getId())
                .title("Second Test Title")
                .description(createdSchedule.getDescription())
                .author("Author2")
                .createdAt(createdSchedule.getCreatedAt())
                .updatedAt(LocalDateTime.of(2024, 07, 11,00,00))  // 최신 날짜
                .password(createdSchedule.getPassword())
                .build();

        // 4. 업데이트 수행
        ScheduleDto updated = scheduleService.updatedDateByAuthorAndTitle(updateSchedule);

        // 5. 검증
        assertThat(updated.getId()).isEqualTo(createdSchedule.getId());
        assertThat(updated.getTitle()).isEqualTo("Second Test Title");
        assertThat(updated.getDescription()).isEqualTo(createdSchedule.getDescription());
        assertThat(updated.getAuthor()).isEqualTo("Author2");
        assertThat(updated.getPassword()).isEqualTo(createdSchedule.getPassword());
    }

}