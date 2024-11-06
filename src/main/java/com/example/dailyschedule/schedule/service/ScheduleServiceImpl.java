package com.example.dailyschedule.schedule.service;

import com.example.dailyschedule.error.CustomException;
import com.example.dailyschedule.error.type.ErrorCode;
import com.example.dailyschedule.member.converter.MemberConverter;
import com.example.dailyschedule.member.dto.MemberDto;
import com.example.dailyschedule.member.entity.Member;
import com.example.dailyschedule.member.repository.MemberRepository;
import com.example.dailyschedule.member.service.MemberService;
import com.example.dailyschedule.schedule.converter.ScheduleConverter;
import com.example.dailyschedule.schedule.dto.SearchDto;
import com.example.dailyschedule.schedule.dto.SingleDateScheduleDto;
import com.example.dailyschedule.schedule.dto.UpdateScheduleDto;
import com.example.dailyschedule.schedule.entity.Schedule;
import com.example.dailyschedule.schedule.dto.ScheduleDto;
import com.example.dailyschedule.schedule.repository.ScheduleRepositoryImpl;
import com.example.dailyschedule.schedule.validation.ScheduleValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
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

    //생성
    @Transactional
    public ScheduleDto create(MemberDto memberDto, ScheduleDto scheduleDto) {
        if (memberDto == null || memberDto.getId() == null) {
            log.error("잘못된 회원 정보입니다. MemberDto: {}", memberDto);
            throw new CustomException(ErrorCode.INVALID_MEMBER_INFO);
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


    //update
    @Transactional
    public ScheduleDto update(MemberDto memberDto, ScheduleDto scheduleDto) {
        Member member = memberConverter.toEntity(memberService.findById(memberDto.getId()));
        Schedule updateSchedule = scheduleRepositoryImpl.updateSchedule(member, scheduleConverter.toEntityIncludeMember(scheduleDto, member));
        return scheduleConverter.toDto(updateSchedule);
    }

    //id 조회
    @Transactional(readOnly = true)
    public ScheduleDto findById(Long id) {
        Schedule existId = scheduleValidation.validateExistId(id);
        return scheduleConverter.toDto(existId);
    }

    //수정일과 작성자 명으로 조회
    @Transactional(readOnly = true)
    public Page<ScheduleDto> findByUpdatedDateAndAuthor(Date updatedAt, String author, SearchDto searchDto) {
        scheduleValidation.validateUpdateDateAndAuthor(updatedAt, author, searchDto);

        // 모든 결과를 리스트 형태로 반환
        Page<Schedule> schedules = scheduleRepositoryImpl.findSchedulesByUpdatedDateAndAuthor(updatedAt, author, searchDto);
        if (schedules == null) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        return schedules.map(scheduleConverter::toDto);
    }

    //내림차순 조회
    @Transactional(readOnly = true)
    public Page<ScheduleDto> findByUpdatedDateDesc(SearchDto searchDto) {
        Page<Schedule> byUpdatedDateByDesc = scheduleRepositoryImpl.findAllOrderByUpdatedDateDesc(searchDto);
        // Schedule을 ScheduleDto로 변환 후 Page<ScheduleDto>로 반환
        return byUpdatedDateByDesc.map(scheduleConverter::toDto);
    }

    //날짜로 조회
    @Transactional(readOnly = true)
    public Page<ScheduleDto> findByDate(Date date, SearchDto searchDto) {
        Page<Schedule> findDates = scheduleRepositoryImpl.findByDate(date, searchDto);
        if (findDates.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        return findDates.map(scheduleConverter::toDto);
    }

    //Lv2
    //update 날짜와 작성자 명으로 조회
    @Transactional
    public ScheduleDto updateTitleAndAuthor(Long scheduleId, UpdateScheduleDto updatedScheduleDto) {
        // 기존 일정 조회
        Schedule existingSchedule = scheduleRepositoryImpl.findScheduleById(scheduleId);
        // 변경 사항을 포함한 새로운 Schedule 생성
        Schedule updatedSchedule = scheduleValidation.validateAndPrepareUpdatedSchedule(updatedScheduleDto, existingSchedule);
        // 업데이트된 스케줄을 저장소에 반영
        scheduleRepositoryImpl.updateSchedule(existingSchedule.getMember(), updatedSchedule);
        // DTO로 변환하여 반환
        return scheduleConverter.toDto(updatedSchedule);
    }


    //아이디 삭제
    @Transactional
    public void deleteById(Long id, String password) {
        Schedule existSchedule = scheduleRepositoryImpl.findScheduleById(id);
        scheduleValidation.deleteByScheduleById(id, password, existSchedule);
        scheduleRepositoryImpl.deleteScheduleById(existSchedule.getId());
    }

    //Lv3
    @Transactional(readOnly = true)
    public Page<ScheduleDto> findSchedulesByMemberId(SearchDto searchDto, Long memberId, Long scheduleId) {
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

    //선택 일정 조회 (선택한 일정 정보 불러오기)
    @Transactional(readOnly = true)
    public SingleDateScheduleDto findDateById(Long id, String field, Date date) {
        Schedule schedule = scheduleValidation.validateExistId(id);
        Schedule findDate = scheduleRepositoryImpl.findDateById(schedule.getId(), field, date);

        return getSingleDateScheduleDto(field, findDate);
    }

    private static SingleDateScheduleDto getSingleDateScheduleDto(String field, Schedule findDate) {
        Date selectDate;

        switch (field) {
            case "created_at":
                selectDate = findDate.getCreatedAt();
                break;
            case "updated_at":
                selectDate = findDate.getUpdatedAt();
                break;
            case "deleted_at":
                selectDate = findDate.getDeletedAt();
            default:
                throw new IllegalArgumentException("유효하지 않은 필드 이름입니다.");
        }

        return new SingleDateScheduleDto(findDate.getId(), findDate.getTitle(), findDate.getAuthor(), selectDate);
    }
}