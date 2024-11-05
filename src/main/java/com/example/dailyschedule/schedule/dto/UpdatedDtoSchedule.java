package com.example.dailyschedule.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UpdatedDtoSchedule {
    private String title;
    private String author;
    private String password;
}
