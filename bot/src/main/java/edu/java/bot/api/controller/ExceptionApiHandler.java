package edu.java.bot.api.controller;

import edu.java.response.ApiErrorResponse;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionApiHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleException(MethodArgumentNotValidException exception) {
        String message = exception.getFieldErrors().stream()
            .map(fieldError -> "field:%s; error:%s".formatted(fieldError.getField(), fieldError.getDefaultMessage()))
            .collect(Collectors.joining(" "));
        return new ApiErrorResponse(
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            exception.getClass().getSimpleName(),
            message
        );
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleException(Throwable throwable) {
        return new ApiErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            throwable.getClass().getSimpleName(),
            throwable.getMessage()
        );
    }
}
