package com.example.dailyschedule.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

//Page dto
@Getter
@Builder
@AllArgsConstructor
public class SearchDto {
    @Builder.Default
    private int page = 1; // 기본 페이지 번호
    @Builder.Default
    private int pageSize = 10; // 기본 페이지 크기
    @Builder.Default
    private int recordSize = 10;  // 페이지당 기본 출력 개수

    public SearchDto() {
        this.page = 1;
        this.pageSize = 10;
        this.recordSize = 10;
    }

    //몇개까지 주세요
    public int getLimit() {
        return recordSize > 0 ? recordSize : 10;  // 기본 limit을 10으로 설정
    }

    //어디부터 시작 0 부터 / 시작 어디로 볼 위치
    public int getOffset() {
        int safePage = Math.max(1, page);
        int safePageSize = Math.max(1, pageSize);
        return (safePage - 1) * safePageSize;
    }
}
