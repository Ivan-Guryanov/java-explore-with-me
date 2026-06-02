package ru.practicum.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class ErrorHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT) //409
    public ApiError handleConflictException(final ConflictException e) {
        return ApiError.builder()
                .message(e.getMessage())
                .reason("The required object was not found.")
                .status(HttpStatus.NOT_FOUND.name())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) //404
    public ApiError handleNotFoundException(final NotFoundException e) {
        return ApiError.builder()
                .message(e.getMessage())
                .reason("The required object was not found.")
                .status(HttpStatus.NOT_FOUND.name())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public ApiError handleValidationException(final ValidationException e) {
        return ApiError.builder()
                .message(e.toString())
                .reason("Incorrectly made request.")
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT) //409
    public ApiError handleDataIntegrityViolation(final DataIntegrityViolationException e) {
        return ApiError.builder()
                .message(e.getMessage())
                .reason("Integrity constraint has been violated.")
                .status(HttpStatus.CONFLICT.name())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public ApiError handleValidationException(final MethodArgumentNotValidException e) {
        StringBuilder errorMessage = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errorMessage.append("Field: ").append(error.getField())
                        .append(". Error: ").append(error.getDefaultMessage()).append("; ")
        );

        return ApiError.builder()
                .message(errorMessage.toString())
                .reason("Incorrectly made request.")
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

}