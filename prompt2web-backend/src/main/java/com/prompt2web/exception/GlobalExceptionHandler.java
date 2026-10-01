package com.prompt2web.exception;

import com.prompt2web.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException exception
    ) {

        ErrorResponse errorResponse = new ErrorResponse(
                exception.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException exception
    ) {

        ErrorResponse errorResponse = new ErrorResponse(
                exception.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception
    ) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField() + ": " + error.getDefaultMessage()
                )
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = new ErrorResponse(
                message,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }







}