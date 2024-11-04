package com.example.dailyschedule.schedule.service;

import com.example.dailyschedule.error.CustomException;
import com.example.dailyschedule.error.type.ErrorCode;
import com.example.dailyschedule.member.converter.MemberConverter;
import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.member.repository.MemberRepository;
import com.example.dailyschedule.member.service.MemberService;
import com.example.dailyschedule.schedule.converter.ScheduleConverter;
import com.example.dailyschedule.schedule.dto.CombinedScheduleDto;
import com.example.dailyschedule.schedule.dto.SearchDto;
import com.example.dailyschedule.schedule.entity.Schedule;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.repository.ScheduleRepositoryImpl;
import com.example.dailyschedule.schedule.validation.ScheduleValidation;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


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

    @Transactional
    public ScheduleDto update(MemberDto memberDto, ScheduleDto scheduleDto) {
        Member member = memberConverter.toEntity(memberService.findByUserId(memberDto.getUserId()));
        Schedule updateSchedule = scheduleRepositoryImpl.updateSchedule(member, scheduleConverter.toEntity(scheduleDto));
        return scheduleConverter.toDto(updateSchedule);
    }

    @Transactional(readOnly = true)
    public ScheduleDto findById(Long id) {
        Schedule existId = scheduleValidation.validateExistId(id);
        return scheduleConverter.toDto(existId);
    }

    @Transactional(readOnly = true)
    public Page<ScheduleDto> findByUpdatedDateAndAuthor(LocalDateTime updatedAt, String author, SearchDto searchDto) {
        scheduleValidation.validateUpdateDateAndAuthor(updatedAt, author, searchDto);

        // 모든 결과를 리스트 형태로 반환
        Page<Schedule> schedules = scheduleRepositoryImpl.findSchedulesByUpdatedDateAndAuthor(updatedAt, author, searchDto);
        if (schedules == null) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        return schedules.map(scheduleConverter::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ScheduleDto> findByUpdatedDateDesc(SearchDto searchDto) {
        Page<Schedule> byUpdatedDateByDesc = scheduleRepositoryImpl.findAllOrderByUpdatedDateDesc(searchDto);
      // Schedule을 ScheduleDto로 변환 후 Page<ScheduleDto>로 반환
        return byUpdatedDateByDesc.map(scheduleConverter::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ScheduleDto> findByDate(LocalDateTime date, SearchDto searchDto) {
        Page<Schedule> findDates = scheduleRepositoryImpl.findByDate(date, searchDto);
        if (findDates.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        return findDates.map(scheduleConverter::toDto);
    }

    //Lv2
    @Transactional
    public ScheduleDto updateTitleAndAuthor(Long scheduleId, CombinedScheduleDto combinedScheduleDto) {
        Member member = memberConverter.toEntity(memberService.findByUserId(combinedScheduleDto.getMemberDto().getUserId()));
        Schedule existingSchedule = scheduleRepositoryImpl.findScheduleById(scheduleId);

        // validateAndPrepareUpdatedSchedule에서 업데이트된 Schedule을 반환받아 저장
        Schedule updatedSchedule = scheduleValidation.validateAndPrepareUpdatedSchedule(combinedScheduleDto, existingSchedule);

        // 업데이트된 스케줄을 저장소에 반영
        scheduleRepositoryImpl.updateSchedule(member, updatedSchedule);

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
    public Page<ScheduleDto> findSchedulesByMemberId(SearchDto searchDto,Long memberId, Long scheduleId) {
        // 검증: 해당 회원과 스케줄이 존재하는지 확인
        scheduleValidation.validationOfFindScheduleByMemberId(
                ScheduleDto.builder().id(scheduleId).build(),
                MemberDto.builder().id(memberId).build()
        );

        Page<Schedule> schedules = scheduleRepositoryImpl.findSchedulesByMemberId(memberId, searchDto);
        if (schedules.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        return schedules.map(scheduleConverter::toDto);
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
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        return scheduleConverter.toDto(schedule);
    }
}
