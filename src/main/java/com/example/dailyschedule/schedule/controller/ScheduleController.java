package com.example.dailyschedule.schedule.controller;

import com.example.dailyschedule.schedule.dto.DeleteScheduleRequest;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.dto.UpdatedDtoSchedule;
import com.example.dailyschedule.schedule.service.ScheduleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleServiceImpl scheduleService;

    public ScheduleController(ScheduleServiceImpl scheduleService) {
        this.scheduleService = scheduleService;
    }

    //스케줄 Id 단건 조회
    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> findById(@PathVariable Long scheduleId) {

        try {
            ScheduleDto findSchedule = scheduleService.findById(scheduleId);
            return ResponseEntity.status(HttpStatus.OK).body(findSchedule);
        } catch (IllegalArgumentException e) {
            log.error("존재하지 않는 사용자 입니다 : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //수정일과 작성자 명으로 스케줄 조회
    @GetMapping("/search")
    public ResponseEntity<?> findScheduleByUpdatedDateAndAuthor(
            @RequestParam("updatedAt") Date updatedAt,
            @RequestParam("author") String author) {
        try {
            List<ScheduleDto> findSchedules = scheduleService.findByUpdatedDateAndAuthor(updatedAt, author);
            return ResponseEntity.status(HttpStatus.OK).body(findSchedules);
        } catch (IllegalArgumentException e) {
            log.error("해당 정보를 찾을 수 없습니다: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //해당 날짜 조회
    @GetMapping("/date")
    public ResponseEntity<?> findByUpdatedDate(
            @RequestParam Date updatedAt) {
        try {
            List<ScheduleDto> findDate = scheduleService.findByDate(updatedAt);
            return ResponseEntity.ok(findDate);

        } catch (IllegalArgumentException e) {
            log.error("해당 날짜를 조회할 수 없습니다: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

    //내림차순 조회
    @GetMapping("/dateDesc")
    public ResponseEntity<?> findByUpdatedDateDesc() {
        try {
            List<ScheduleDto> findDate = scheduleService.findByUpdatedDateDesc();
            return ResponseEntity.ok(findDate);
        } catch (IllegalArgumentException e) {
            log.error("해당 날짜를 내림차순으로 조회 할 수 없습니다. : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

    //스케줄 생성
    @PostMapping("/")
    public ResponseEntity<?> createSchedule(@RequestBody ScheduleDto scheduleDto) {

        try {
            ScheduleDto createSchedule = scheduleService.create(scheduleDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createSchedule);
        } catch (IllegalArgumentException e) {
            log.error("일정을 생성하는데 실패했습니다.. : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //스케줄 update (title, author)
    @PutMapping("/{scheduleId}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Long scheduleId,
            @RequestBody UpdatedDtoSchedule updatedDtoSchedule) {
        try {
            ScheduleDto updatedSchedules = scheduleService.updateTitleAndAuthor(scheduleId, updatedDtoSchedule);
            return ResponseEntity.status(HttpStatus.OK).body(updatedSchedules);
        } catch (IllegalArgumentException e) {
            log.error("일정을 수정하는데 실패했습니다. : {} ", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    //스케줄 삭제
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(
            @PathVariable Long scheduleId,
            @RequestBody DeleteScheduleRequest deleteScheduleRequest) {
        try {
            scheduleService.deleteById(scheduleId, deleteScheduleRequest.getPassword());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IllegalArgumentException e) {
            log.error("일정을 삭제하는데 실패했습니다. : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
