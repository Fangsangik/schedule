package com.example.dailyschedule.service;

import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.dto.UpdatedDtoSchedule;
import com.example.dailyschedule.schedule.repository.ScheduleRepositoryImpl;
import com.example.dailyschedule.schedule.service.ScheduleServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
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
    @Transactional
    void create() {
        schedule = ScheduleDto.builder()
                .id(1L)
                .title("test title")
                .description("test description")
                .author("test author")
                .createdAt(new Date(System.currentTimeMillis()))
                .updatedAt(new Date(System.currentTimeMillis()))
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
                .createdAt(new Date(System.currentTimeMillis()))
                .updatedAt(new Date(System.currentTimeMillis()))
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
                .createdAt(new Date(System.currentTimeMillis()))
                .updatedAt(new Date(System.currentTimeMillis()))
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
                .updatedAt(new Date(System.currentTimeMillis()))
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
                .createdAt(new Date(System.currentTimeMillis()))
                .updatedAt(new Date(System.currentTimeMillis()))
                .password("TestPassword")
                .build();

        ScheduleDto createdSchedule = scheduleService.create(schedule);
        assertNotNull(createdSchedule);

        scheduleService.deleteById(createdSchedule.getId(), "TestPassword");

        assertThrows(IllegalArgumentException.class, () -> {
            scheduleService.findById(createdSchedule.getId());
        });
    }

    @Test
    @Transactional
    void findByUpdatedDateDesc() {
        scheduleRepository.deleteAll();

        ScheduleDto schedule1 = ScheduleDto.builder()
                .id(1L)
                .title("First Test Title")
                .description("First Test Description")
                .author("Author1")
                .createdAt(new Date(2024, 07, 01))
                .updatedAt(new Date(2024, 07, 10))
                .password("password1")
                .build();

        ScheduleDto schedule2 = ScheduleDto.builder()
                .title("Second Test Title")
                .description("Second Test Description")
                .author("Author2")
                .createdAt(new Date(2024, 06, 01))
                .updatedAt(new Date(2024, 07, 05))
                .password("password2")
                .build();

        scheduleService.create(schedule1);
        scheduleService.create(schedule2);

        List<ScheduleDto> scheduleDtoList = scheduleService.findByUpdatedDateDesc();
        assertFalse(scheduleDtoList.isEmpty());
        assertThat(scheduleDtoList.size()).isEqualTo(2);
        assertThat(scheduleDtoList.get(0).getUpdatedAt()).isEqualTo(new Date(2024, 07, 10));
        assertThat(scheduleDtoList.get(1).getUpdatedAt()).isEqualTo(new Date(2024, 07, 05));
    }

    @Test
    @Transactional
    void updatedDateByAuthorAndTitle() {
        // 기존 데이터 삭제
        scheduleRepository.deleteAll();

        // 첫 번째 일정 생성
        ScheduleDto schedule1 = ScheduleDto.builder()
                .id(1L)
                .title("Original Test Title")
                .description("Original Test Description")
                .author("Original Author")
                .createdAt(new Date(2024, 07, 01))
                .updatedAt(new Date(2024, 07, 10))
                .password("password1")
                .build();

        // 일정 생성 및 생성된 일정의 ID 확인
        ScheduleDto createdSchedule = scheduleService.create(schedule1);

        // 업데이트할 제목과 작성자 정보 설정
        UpdatedDtoSchedule updatedScheduleDto = UpdatedDtoSchedule.builder()
                .title("Updated Test Title")               // 새로운 제목
                .author("Updated Author")                  // 새로운 작성자
                .password(createdSchedule.getPassword())
                .build();


        // 제목과 작성자명 업데이트
        ScheduleDto updatedSchedule = scheduleService.updateTitleAndAuthor(createdSchedule.getId(), updatedScheduleDto);

        // 업데이트된 제목과 작성자를 검증
        assertThat(updatedSchedule.getTitle()).isEqualTo("Updated Test Title");
        assertThat(updatedSchedule.getAuthor()).isEqualTo("Updated Author");
    }


    @Test
    @Transactional
    void findDateTest() {
        scheduleRepository.deleteAll();

        ScheduleDto schedule1 = ScheduleDto.builder()
                .title("Original Test Title")
                .description("Original Test Description")
                .author("Original Author")
                .createdAt(new Date(2024, 7, 01))
                .updatedAt(new Date(2024, 7, 10))
                .password("password1")
                .build();

        ScheduleDto scheduleDto1 = scheduleService.create(schedule1);
        List<ScheduleDto> createdAtDate = scheduleService.findByDate(scheduleDto1.getCreatedAt());

        // 리스트가 비어 있지 않은지 확인
        assertNotNull(createdAtDate);
        assertFalse(createdAtDate.isEmpty());

        // 리스트의 첫 번째 ScheduleDto의 createdAt 값이 예상 값과 일치하는지 확인
        assertThat(createdAtDate.get(0).getCreatedAt()).isEqualTo(new Date(2024, 7, 1));
    }

    @Test
    @Transactional
    void findByUpdateDateAndAuthor() {
        scheduleRepository.deleteAll();

        ScheduleDto schedule1 = ScheduleDto.builder()
                .title("Original Test Title")
                .description("Original Test Description")
                .author("Original Author")
                .createdAt(new Date(2024, 7, 01))
                .updatedAt(new Date(2024, 7, 10))
                .password("password1")
                .build();

        ScheduleDto cratedSchedule = scheduleService.create(schedule1);
        List<ScheduleDto> findSchedule = scheduleService.findByUpdatedDateAndAuthor(schedule1.getUpdatedAt(), cratedSchedule.getAuthor());
        assertThat(findSchedule.get(0).getUpdatedAt()).isEqualTo(cratedSchedule.getUpdatedAt());
        assertThat(findSchedule.get(0).getTitle()).isEqualTo("Original Test Title");
    }
}