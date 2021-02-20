package com.amberlight.wallet.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for errors
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDto {

    private String error;
    private String message;
    private Integer code;

    public ErrorDto(String error) {
        this.error = error;
    }

    public ErrorDto(String error, Integer code) {
        this.error = error;
        this.code = code;
    }

    public ErrorDto(List<ObjectError> allErrors, String error) {
        this.error = error;
        String temp = allErrors.stream().map(e -> {
            if (e instanceof FieldError) {
                return "{\"field\":\"" + ((FieldError) e).getField() + "\",\"defaultMessage\":\""
                        + e.getDefaultMessage() + "\"}";
            } else {
                return "{\"object\":\"" + e.getObjectName() + "\",\"defaultMessage\":\""
                        + e.getDefaultMessage() + "\"}";
            }
        }).collect(Collectors.joining(","));
        this.message = "[" + temp + "]";
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
