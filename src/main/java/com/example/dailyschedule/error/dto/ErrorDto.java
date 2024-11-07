package com.example.dailyschedule.error.dto;

import com.example.dailyschedule.error.type.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorDto {
    private String message;
    private int status;

    public ErrorDto(String message, int status, HttpStatus httpStatus) {
    }

    public ErrorDto(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public ErrorDto(ErrorCode error) {
        this.message = error.getMessage();
        this.status = error.getStatus();
    }
}
