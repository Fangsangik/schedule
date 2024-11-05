package com.example.dailyschedule.schedule.service;

import com.example.dailyschedule.member.converter.MemberConverter;
import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.member.repository.MemberRepository;
import com.example.dailyschedule.member.service.MemberService;
import com.example.dailyschedule.schedule.converter.ScheduleConverter;
import com.example.dailyschedule.schedule.dto.UpdateScheduleDto;
import com.example.dailyschedule.schedule.entity.Schedule;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.repository.ScheduleRepositoryImpl;
import com.example.dailyschedule.schedule.validation.ScheduleValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScheduleServiceImpl {

    private final ScheduleRepositoryImpl scheduleRepositoryImpl;
    private final ScheduleConverter scheduleConverter;
    private final ScheduleValidation scheduleValidation;
    private final MemberService memberService;
    private final MemberConverter memberConverter;

    //생성자 주입
    public ScheduleServiceImpl(ScheduleRepositoryImpl scheduleRepositoryImpl, ScheduleConverter scheduleConverter, MemberRepository memberRepository, MemberConverter memberConverter, MemberService memberService) {
        this.scheduleRepositoryImpl = scheduleRepositoryImpl;
        this.scheduleConverter = scheduleConverter;
        this.memberService = memberService;
        this.scheduleValidation = new ScheduleValidation(scheduleRepositoryImpl, memberRepository);
        this.memberConverter = memberConverter;
    }

    @Transactional
    public ScheduleDto create(MemberDto memberDto, ScheduleDto scheduleDto) {
        if (memberDto == null || memberDto.getId() == null) {
            log.error("잘못된 회원 정보입니다. MemberDto: {}", memberDto);
            throw new IllegalArgumentException("해당 회원이 없습니다.");
        }

        // MemberService를 통해 Member 정보 가져오기
        Member member = memberConverter.toEntity(memberService.findById(memberDto.getId()));

        // 중복 ID 검증
        scheduleValidation.validationOfDuplicateId(scheduleDto.getId());

        // Member 객체를 함께 전달하여 Schedule 생성
        Schedule saveSchedule = scheduleRepositoryImpl.createSchedule(scheduleConverter.toEntityIncludeMember(scheduleDto, member), member);

        // ScheduleDto 반환
        return scheduleConverter.toDto(saveSchedule);
    }

    @Transactional(readOnly = true)
    public ScheduleDto findById(Long id) {
        Schedule existId = scheduleValidation.validateExistId(id);
        return scheduleConverter.toDto(existId);
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> findByUpdatedDateAndAuthor(Date updatedAt, String author) {
        scheduleValidation.validateUpdateDateAndAuthor(updatedAt, author);

        // 모든 결과를 리스트 형태로 반환
        List<Schedule> schedules = scheduleRepositoryImpl.findSchedulesByUpdatedDateAndAuthor(updatedAt, author);
        return schedules.stream()
                .map(scheduleConverter::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> findByUpdatedDateDesc() {
        List<Schedule> byUpdatedDateByDesc = scheduleRepositoryImpl.findAllOrderByUpdatedDateDesc();
        List<ScheduleDto> scheduleDtos = new ArrayList<>();
        for (Schedule schedule : byUpdatedDateByDesc) {
            scheduleDtos.add(scheduleConverter.toDto(schedule));
        }
        return scheduleDtos;
    }

    @Transactional
    public ScheduleDto update(MemberDto memberDto, ScheduleDto scheduleDto) {
        Member member = memberConverter.toEntity(memberService.findById(memberDto.getId()));
        Schedule updateSchedule = scheduleRepositoryImpl.updateSchedule(member, scheduleConverter.toEntityIncludeMember(scheduleDto, member));
        return scheduleConverter.toDto(updateSchedule);
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> findByDate(Date date) {
        List<Schedule> findDates = scheduleRepositoryImpl.findByDate(date);
        if (findDates.isEmpty()) {
            throw new IllegalArgumentException("해당 날짜에 대한 값이 존재하지 않습니다.");
        }
        List<ScheduleDto> scheduleDtoList = new ArrayList<>();
        for (Schedule findDate : findDates) {
            scheduleDtoList.add(scheduleConverter.toDto(findDate));
        }
        return scheduleDtoList;
    }

    //Lv2
    @Transactional
    public ScheduleDto updateTitleAndAuthor(Long scheduleId, UpdateScheduleDto updateScheduleDto) {
        // 기존 일정 조회
        Schedule existingSchedule = scheduleRepositoryImpl.findScheduleById(scheduleId);
        // 변경 사항을 포함한 새로운 Schedule 생성
        Schedule updatedSchedule = scheduleValidation.validateAndPrepareUpdatedSchedule(updateScheduleDto, existingSchedule);
        // 업데이트된 스케줄을 저장소에 반영
        scheduleRepositoryImpl.updateSchedule(existingSchedule.getMember(), updatedSchedule);
        // DTO로 변환하여 반환
        return scheduleConverter.toDto(updatedSchedule);
    }


    @Transactional
    public void deleteById(Long id, String password) {
        Schedule existSchedule = scheduleRepositoryImpl.findScheduleById(id);
        scheduleValidation.deleteByScheduleById(id, password, existSchedule);
        scheduleRepositoryImpl.deleteScheduleById(existSchedule.getId());
    }

    //Lv3
    @Transactional(readOnly = true)
    public List<ScheduleDto> findSchedulesByMemberId(Long memberId, Long scheduleId) {
        // 검증: 해당 회원과 스케줄이 존재하는지 확인
        scheduleValidation.validationOfFindScheduleByMemberId(
                ScheduleDto.builder().id(scheduleId).build(),
                MemberDto.builder().id(memberId).build()
        );

        List<Schedule> schedules = scheduleRepositoryImpl.findSchedulesByMemberId(memberId);
        if (schedules.isEmpty()) {
            throw new IllegalArgumentException("해당 회원의 아이디에 할당된 스케줄이 없습니다.");
        }

        return schedules.stream()
                .map(scheduleConverter::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScheduleDto findScheduleByMemberId(Long memberId, Long scheduleId) {
        // 검증: 해당 회원과 스케줄이 존재하는지 확인
        scheduleValidation.validationOfFindScheduleByMemberId(
                ScheduleDto.builder().id(scheduleId).build(),
                MemberDto.builder().id(memberId).build()
        );

        // 검증이 통과하면 조회 진행
        Schedule schedule = scheduleRepositoryImpl.findSingleScheduleByMemberId(memberId);
        if (schedule == null) {
            throw new IllegalArgumentException("해당 회원의 아이디에 할당된 스케줄이 없습니다.");
        }

        return scheduleConverter.toDto(schedule);
    }

}
