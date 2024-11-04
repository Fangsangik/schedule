package com.example.dailyschedule.schedule.controller;

import com.example.dailyschedule.error.CustomException;
import com.example.dailyschedule.error.type.ErrorCode;
import com.example.dailyschedule.schedule.dto.CombinedScheduleDto;
import com.example.dailyschedule.schedule.dto.DeleteScheduleRequest;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.dto.SearchDto;
import com.example.dailyschedule.schedule.service.ScheduleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleServiceImpl scheduleService;

    public ScheduleController(ScheduleServiceImpl scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> findById(@PathVariable Long scheduleId) {

        try {
            ScheduleDto findSchedule = scheduleService.findById(scheduleId);
            return ResponseEntity.status(HttpStatus.OK).body(findSchedule);
        } catch (IllegalArgumentException e) {
            log.error("존재하지 않는 사용자 입니다 : {}", e.getMessage());
            throw new CustomException(ErrorCode.ID_NOT_FOUND);

        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> findScheduleByUpdatedDateAndAuthor(
            @RequestParam("updatedAt") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime updatedAt,
            @RequestParam("author") String author, SearchDto searchDto) {
        try {
            Page<ScheduleDto> findSchedules = scheduleService.findByUpdatedDateAndAuthor(updatedAt, author, searchDto);
            return ResponseEntity.status(HttpStatus.OK).body(findSchedules);
        } catch (IllegalArgumentException e) {
            log.error("해당 정보를 찾을 수 없습니다: {}", e.getMessage());
            throw new CustomException(ErrorCode.NOT_FOUND);
        }
    }

    @GetMapping("/date")
    public ResponseEntity<?> findByUpdatedDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime updatedAt,
            SearchDto searchDto) {
        try {
            Page<ScheduleDto> findDate = scheduleService.findByDate(updatedAt, searchDto);
            return ResponseEntity.ok(findDate);

        } catch (IllegalArgumentException e) {
            log.error("해당 날짜를 조회할 수 없습니다: {}", e.getMessage());
            throw new CustomException(ErrorCode.NOT_FOUND);
        }
    }

    @GetMapping("/dateDesc")
    public ResponseEntity<?> findByUpdatedDateDesc(SearchDto searchDto) {
        try {
            Page<ScheduleDto> findDate = scheduleService.findByUpdatedDateDesc(searchDto);
            return ResponseEntity.ok(findDate);
        } catch (IllegalArgumentException e) {
            log.error("해당 날짜를 내림차순으로 조회 할 수 없습니다. : {}", e.getMessage());
            throw new CustomException(ErrorCode.ID_NOT_FOUND);
        }
    }

    @GetMapping("/{memberId}/{scheduleId}")
    public ResponseEntity<?> findSchedulesByMemberId(
            @PathVariable Long memberId,
            @PathVariable Long scheduleId,
            SearchDto searchDto
    ) {
        try {
            Page<ScheduleDto> findSchedules = scheduleService.findSchedulesByMemberId(searchDto, scheduleId, memberId);
            return ResponseEntity.status(HttpStatus.OK).body(findSchedules);
        } catch (IllegalArgumentException e) {
            log.error("조회에 실패했습니다. : {} ", e.getMessage());
            throw new CustomException(ErrorCode.ID_NOT_FOUND);

        }
    }

    @GetMapping("/{memberId}/schedules/{scheduleId}")
    public ResponseEntity<?> findScheduleByMemberId(@PathVariable Long memberId,
                                                    @PathVariable Long scheduleId
    ) {
        try {
            ScheduleDto findSchedule = scheduleService.findScheduleByMemberId(memberId, scheduleId);
            return ResponseEntity.status(HttpStatus.OK).body(findSchedule);
        } catch (IllegalArgumentException e) {
            log.error("조회에 실패했습니다. : {} ", e.getMessage());
            throw new CustomException(ErrorCode.ID_NOT_FOUND);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createSchedule(
            @RequestBody CombinedScheduleDto combinedScheduleDto) {

        try {
            ScheduleDto createSchedule = scheduleService.create(combinedScheduleDto.getMemberDto(), combinedScheduleDto.getScheduleDto());
            return ResponseEntity.status(HttpStatus.CREATED).body(createSchedule);
        } catch (IllegalArgumentException e) {
            log.error("일정을 생성하는데 실패했습니다.. : {}", e.getMessage());
            throw new CustomException(ErrorCode.NOT_FOUND);
        }
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Long scheduleId,
            @RequestBody CombinedScheduleDto combinedScheduleDto) {
        try {
            ScheduleDto updatedSchedules = scheduleService.updateTitleAndAuthor(scheduleId, combinedScheduleDto);
            return ResponseEntity.status(HttpStatus.OK).body(updatedSchedules);
        } catch (IllegalArgumentException e) {
            log.error("일정을 수정하는데 실패했습니다. : {} ", e.getMessage());
            throw new CustomException(ErrorCode.NOT_FOUND);
        }
    }


    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(
            @PathVariable Long scheduleId,
            @RequestBody DeleteScheduleRequest deleteScheduleRequest) {
        try {
            scheduleService.deleteById(scheduleId, deleteScheduleRequest.getPassword());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IllegalArgumentException e) {
            log.error("일정을 삭제하는데 실패했습니다. : {}", e.getMessage());
            throw new CustomException(ErrorCode.NOT_FOUND);
        }
    }
}