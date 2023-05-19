package com.gvormbrock.test.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used by GlobalExceptionHandler to display REST error messages
 */
@Setter
@Getter
public class ErrorDetails {
    private int errorCode;
    private String errorMessage;
    private String devErrorMessage;
    private Map<String, Object> additionalData = new HashMap<>();
}
