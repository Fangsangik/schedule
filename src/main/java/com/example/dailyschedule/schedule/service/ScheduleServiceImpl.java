package com.example.dailyschedule.schedule.service;

import com.example.dailyschedule.member.converter.MemberConverter;
import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.member.repository.MemberRepository;
import com.example.dailyschedule.member.service.MemberService;
import com.example.dailyschedule.schedule.converter.ScheduleConverter;
import com.example.dailyschedule.schedule.dto.SearchDto;
import com.example.dailyschedule.schedule.entity.Schedule;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.repository.ScheduleRepositoryImpl;
import com.example.dailyschedule.schedule.validation.ScheduleValidation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    private final MemberConverter memberConverter;
    private final MemberService memberService;

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
    public ScheduleDto findByUpdatedDateAndAuthor (LocalDateTime updatedAt, String author) {
        Schedule schedule = scheduleRepositoryImpl.findByUpdatedDateAndAuthor(updatedAt, author);
        scheduleValidation.validateUpdateDateAndAuthor(updatedAt, author, schedule);
        return scheduleConverter.toDto(schedule);
    }

    @Transactional(readOnly = true)
    public Page<ScheduleDto> findByUpdatedDateDesc(SearchDto searchDto) {
        Page<Schedule> byUpdatedDateByDesc = scheduleRepositoryImpl.findAllOrderByUpdatedDateDesc(searchDto);

        List<ScheduleDto> scheduleDtos = new ArrayList<>();
        for (Schedule schedule : byUpdatedDateByDesc) {
            ScheduleDto scheduleDto = ScheduleDto.builder()
                    .id(schedule.getId())
                    .author(schedule.getAuthor())
                    .title(schedule.getTitle())
                    .description(schedule.getDescription())
                    .createdAt(schedule.getCreatedAt())
                    .updatedAt(schedule.getUpdatedAt())
                    .deletedAt(schedule.getDeletedAt())
                    .build();
            scheduleDtos.add(scheduleDto);
        }
        return new PageImpl<>(scheduleDtos, byUpdatedDateByDesc.getPageable(), byUpdatedDateByDesc.getTotalElements());
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
    public void deleteById(ScheduleDto scheduleDto) {
        Schedule existSchedule = scheduleRepositoryImpl.findScheduleById(scheduleDto.getId());
        scheduleValidation.deleteByScheduleById(scheduleDto, existSchedule);
        scheduleRepositoryImpl.deleteScheduleById(existSchedule.getId());
    }

    //Lv2
    @Transactional
    public ScheduleDto updateTitleAndAuthor(MemberDto memberDto, ScheduleDto scheduleDto) {
        Member member = memberConverter.toEntity(memberService.findByUserId(memberDto.getUserId()));
        Schedule existingSchedule = scheduleRepositoryImpl.findScheduleById(scheduleDto.getId());
        Schedule updatedScheduleDto = scheduleValidation.updateTitleAndAuthor(scheduleDto, existingSchedule);
        scheduleRepositoryImpl.updateSchedule(member, updatedScheduleDto);
        return scheduleConverter.toDto(updatedScheduleDto);
    }

    //Lv3
    @Transactional(readOnly = true)
    public Page<ScheduleDto> findSchedulesByMemberId(SearchDto searchDto,Long memberId, Long scheduleId) {
        // 검증: 해당 회원과 스케줄이 존재하는지 확인
        scheduleValidation.validationOfFindScheduleByMemberId(
                ScheduleDto.builder().id(scheduleId).build(),
                MemberDto.builder().id(memberId).build()
        );

        Page<Schedule> schedules = scheduleRepositoryImpl.findSchedulesByMemberId(searchDto, memberId);
        if (schedules.isEmpty()) {
            throw new IllegalArgumentException("해당 회원의 아이디에 할당된 스케줄이 없습니다.");
        }

        List<ScheduleDto> scheduleDtos = new ArrayList<>();
        for (Schedule schedule : schedules.getContent()) {
           ScheduleDto scheduleDto = ScheduleDto.builder()
                    .id(schedule.getId())
                    .author(schedule.getAuthor())
                    .title(schedule.getTitle())
                    .description(schedule.getDescription())
                    .createdAt(schedule.getCreatedAt())
                    .updatedAt(schedule.getUpdatedAt())
                    .deletedAt(schedule.getDeletedAt())
                    .password(schedule.getPassword())
                    .build();
           scheduleDtos.add(scheduleDto);
        }

        return new PageImpl<>(scheduleDtos, schedules.getPageable(), schedules.getTotalElements());
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
