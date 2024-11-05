package com.example.dailyschedule.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//DeletedDtp
public class DeleteScheduleRequest {
    private Long id;
    private String password;
}
