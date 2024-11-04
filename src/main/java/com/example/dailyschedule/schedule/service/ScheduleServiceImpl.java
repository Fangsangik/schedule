package com.example.dailyschedule.schedule.service;

import com.example.dailyschedule.member.converter.MemberConverter;
import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.member.repository.MemberRepository;
import com.example.dailyschedule.member.service.MemberService;
import com.example.dailyschedule.schedule.converter.ScheduleConverter;
import com.example.dailyschedule.schedule.dto.CombinedScheduleDto;
import com.example.dailyschedule.schedule.entity.Schedule;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.repository.ScheduleRepositoryImpl;
import com.example.dailyschedule.schedule.validation.ScheduleValidation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        // MemberService를 통해 Member 정보 가져오기
        Member member = memberConverter.toEntity(memberService.findByUserId(memberDto.getUserId()));

        // 중복 ID 검증
        scheduleValidation.validationOfDuplicateId(scheduleDto.getId());

        // Member 객체를 함께 전달하여 Schedule 생성
        Schedule saveSchedule = scheduleRepositoryImpl.createSchedule(scheduleConverter.toEntity(scheduleDto), member);

        // ScheduleDto 반환
        return scheduleConverter.toDto(saveSchedule);
    }

    @Transactional(readOnly = true)
    public ScheduleDto findById(Long id) {
        Schedule existId = scheduleValidation.validateExistId(id);
        return scheduleConverter.toDto(existId);
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> findByUpdatedDateAndAuthor(LocalDateTime updatedAt, String author) {
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
        Member member = memberConverter.toEntity(memberService.findByUserId(memberDto.getUserId()));
        Schedule updateSchedule = scheduleRepositoryImpl.updateSchedule(member, scheduleConverter.toEntity(scheduleDto));
        return scheduleConverter.toDto(updateSchedule);
    }

    @Transactional(readOnly = true)
    public ScheduleDto findByDate(LocalDateTime date) {
        Schedule findDate = scheduleRepositoryImpl.findByDate(date);

        if (findDate == null) {
            throw new IllegalArgumentException("해당 날짜에 대한 값이 존재하지 않습니다.");
        }

        return scheduleConverter.toDto(findDate);
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
