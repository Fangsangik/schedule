package com.example.dailyschedule.schedule.controller;

import com.example.dailyschedule.error.CustomException;
import com.example.dailyschedule.error.type.ErrorCode;
import com.example.dailyschedule.schedule.dto.DeleteScheduleRequest;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.dto.SearchDto;
import com.example.dailyschedule.schedule.dto.UpdatedScheduleDto;
import com.example.dailyschedule.schedule.service.ScheduleServiceImpl;
import com.example.dailyschedule.schedule.dto.SingleDateScheduleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;

@Slf4j
@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleServiceImpl scheduleService;

    public ScheduleController(ScheduleServiceImpl scheduleService) {
        this.scheduleService = scheduleService;
    }

    //아이디 조회
    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> findById(@PathVariable Long scheduleId) {

        try {
            ScheduleDto findSchedule = scheduleService.findById(scheduleId);
            return ResponseEntity.status(HttpStatus.OK).body(findSchedule);
        } catch (CustomException e) {
            log.error("존재하지 않는 사용자 입니다 : {}", e.getMessage());
            throw new CustomException(ErrorCode.ID_NOT_FOUND);

        }
    }

    //updated 날짜와 작성자로 조회
    @GetMapping("/search")
    public ResponseEntity<?> findScheduleByUpdatedDateAndAuthor(
            @RequestParam(required = false) Date updatedAt,
            @RequestParam(required = false) String author, SearchDto searchDto) {
        try {
            Page<ScheduleDto> findSchedules = scheduleService.findByUpdatedDateAndAuthor(updatedAt, author, searchDto);
            return ResponseEntity.status(HttpStatus.OK).body(findSchedules);
        } catch (CustomException e) {
            log.error("해당 정보를 찾을 수 없습니다: {}", e.getMessage());
            if (e.getErrorCode() == ErrorCode.PASSWORD_INCORRECT) {
                log.error("password가 일치하지 않습니다 : {}", e.getMessage());
                throw new CustomException(ErrorCode.PASSWORD_INCORRECT);
            }
        }
        throw new CustomException(ErrorCode.UPDATE_FAILED);
    }

    //update 날짜로 조회
    @GetMapping("/date")
    public ResponseEntity<?> findByUpdatedDate(
            @RequestParam Date updatedAt,
            SearchDto searchDto) {
        try {
            Page<ScheduleDto> findDate = scheduleService.findByDate(updatedAt, searchDto);
            return ResponseEntity.ok(findDate);
        } catch (CustomException e) {
            log.error("해당 날짜를 조회할 수 없습니다: {}", e.getMessage());
            throw new CustomException(ErrorCode.DATE_NOT_FOUND);
        }
    }

    //내림차순 조회
    @GetMapping("/dateDesc")
    public ResponseEntity<?> findByUpdatedDateDesc(SearchDto searchDto) {
        try {
            Page<ScheduleDto> findDate = scheduleService.findByUpdatedDateDesc(searchDto);
            return ResponseEntity.ok(findDate);
        } catch (CustomException e) {
            log.error("해당 날짜를 내림차순으로 조회 할 수 없습니다. : {}", e.getMessage());
            throw new CustomException(ErrorCode.PAGE_DOES_NOT_EXIST);
        }
    }

    //동시 조회 Page
    @GetMapping("/{memberId}/{scheduleId}")
    public ResponseEntity<?> findSchedulesByMemberId(
            @PathVariable Long memberId,
            @PathVariable Long scheduleId,
            SearchDto searchDto
    ) {
        try {
            Page<ScheduleDto> findSchedules = scheduleService.findSchedulesByMemberId(searchDto, scheduleId, memberId);
            return ResponseEntity.status(HttpStatus.OK).body(findSchedules);
        } catch (CustomException e) {
            log.error("조회에 실패했습니다. : {} ", e.getMessage());
            throw new CustomException(ErrorCode.ID_NOT_FOUND);
        }
    }

    //동시 조회
    @GetMapping("/{memberId}/schedules/{scheduleId}")
    public ResponseEntity<?> findScheduleByMemberId(@PathVariable Long memberId,
                                                    @PathVariable Long scheduleId
    ) {
        try {
            ScheduleDto findSchedule = scheduleService.findScheduleByMemberId(memberId, scheduleId);
            return ResponseEntity.status(HttpStatus.OK).body(findSchedule);
        } catch (CustomException e) {
            log.error("조회에 실패했습니다. : {} ", e.getMessage());
            throw new CustomException(ErrorCode.ID_NOT_FOUND);
        }
    }

    // 특정 날짜 필드를 지정하여 일정 조회
    @GetMapping("/date/{scheduleId}")
    public ResponseEntity<?> findByScheduleUpdatedDate(
            @PathVariable Long scheduleId,
            @RequestParam("field") String field,
            @RequestParam Date date) {
        try {
            SingleDateScheduleDto dateById = scheduleService.findDateById(scheduleId, field, date);
            return ResponseEntity.status(HttpStatus.OK).body(dateById);
        } catch (CustomException e) {
            if (e.getErrorCode() == ErrorCode.ID_NOT_FOUND) {
                log.error("해당 ID를 찾을 수 없습니다 : {}" , e.getMessage());
                throw new CustomException(ErrorCode.ID_NOT_FOUND);
            }
            log.error("해당 정보를 찾을 수 없습니다: {}", e.getMessage());
            throw new CustomException(ErrorCode.DATE_NOT_FOUND);
        }
    }

    //생성
    @PostMapping("/")
    public ResponseEntity<?> createSchedule(
            @RequestBody ScheduleDto scheduleDto) {

        try {
            ScheduleDto createSchedule = scheduleService.create(scheduleDto.getMemberDto(), scheduleDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createSchedule);
        } catch (CustomException e) {
            log.error("일정을 생성하는데 실패했습니다.. : {}", e.getMessage());
            throw new CustomException(ErrorCode.CREATION_FAILED);
        }
    }

    //update 날짜
    @PutMapping("/{scheduleId}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Long scheduleId,
            @RequestBody UpdatedScheduleDto updatedScheduleDto) {
        try {
            ScheduleDto updatedSchedules = scheduleService.updateTitleAndAuthor(scheduleId, updatedScheduleDto);
            return ResponseEntity.status(HttpStatus.OK).body(updatedSchedules);
        } catch (CustomException e) {
            if (e.getErrorCode() == ErrorCode.PASSWORD_INCORRECT) {
                log.error("비밀번호를 찾을 수 없습니다. : {}", e.getMessage());
                throw new CustomException(ErrorCode.PASSWORD_INCORRECT);
            } else if (e.getErrorCode() == ErrorCode.ID_NOT_FOUND) {
                log.error("이미 삭제된 정보입니다. : {}", e.getMessage());
                throw new CustomException(ErrorCode.ID_NOT_FOUND);
            }
            log.error("일정을 수정하는데 실패했습니다. : {} ", e.getMessage());
            throw new CustomException(ErrorCode.UPDATE_FAILED);
        }
    }


    //삭제
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(
            @PathVariable Long scheduleId,
            @RequestBody DeleteScheduleRequest deleteScheduleRequest) {
        try {
            scheduleService.deleteById(scheduleId, deleteScheduleRequest.getPassword());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomException e) {
            log.error("일정을 삭제하는데 실패했습니다. : {}", e.getMessage());
            if (e.getErrorCode().equals(ErrorCode.PASSWORD_INCORRECT)) {
                throw new CustomException(ErrorCode.PASSWORD_INCORRECT);
            }
            throw new CustomException(ErrorCode.DELETE_FAILED);
        }
    }
}