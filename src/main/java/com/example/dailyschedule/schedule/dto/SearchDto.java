package com.example.dailyschedule.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SearchDto {
    private int page; //현재 page 번호
    private int pageSize; //화면 하단에 출력한 페이지 사이즈
    private int recordSize; //페이지당 출력 개수

    public SearchDto() {
        this.page = 1;
        this.pageSize = 10;
        this.recordSize = 10;
    }

    //offset의 사용 목적
    //대량의 데이터를 특정 구간의 데이터만 효율적으로 가져올 수 있음.
    //단 대용량 데이터 처리는 한계가 있음
    public int getOffset() {
        return (page - 1) * pageSize;
    }
}
