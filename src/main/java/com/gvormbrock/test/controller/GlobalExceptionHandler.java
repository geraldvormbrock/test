package com.gvormbrock.test.controller;

import com.gvormbrock.test.exception.ErrorDetails;
import com.gvormbrock.test.exception.ErrorServerException;
import com.gvormbrock.test.exception.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<ErrorDetails> servletRequestBindingException(ServletRequestBindingException e) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage(e.getMessage());
        errorDetails.setDevErrorMessage(getStackTraceAsString(e));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDetails> notFoundException(EntityNotFoundException e) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorCode(404);
        errorDetails.setErrorMessage(e.getMessage());
        errorDetails.setDevErrorMessage(getStackTraceAsString(e));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDetails> notFoundException(NotFoundException e) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorCode(e.getErrorCode());
        errorDetails.setErrorMessage(e.getMessage());
        errorDetails.setDevErrorMessage(getStackTraceAsString(e));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ErrorServerException.class)
    public ResponseEntity<ErrorDetails> errorServerException(ErrorServerException e) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorCode(e.getErrorCode());
        errorDetails.setErrorMessage(e.getMessage());
        errorDetails.setDevErrorMessage(getStackTraceAsString(e));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> exception(Exception e) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage(e.getMessage());
        errorDetails.setDevErrorMessage(getStackTraceAsString(e));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
