package edu.java.bot.api.controller;

import edu.java.response.ApiErrorResponse;
import io.micrometer.core.instrument.Counter;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionApiHandler {
    private final Counter errorsCounter;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleException(MethodArgumentNotValidException exception) {
        errorsCounter.increment();
        String message = exception.getFieldErrors().stream()
            .map(fieldError -> "field:%s; error:%s".formatted(fieldError.getField(), fieldError.getDefaultMessage()))
            .collect(Collectors.joining(" "));
        return new ApiErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            exception.getClass().getSimpleName(),
            message
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleException(Exception exception) {
        errorsCounter.increment();
        return new ApiErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            exception.getClass().getSimpleName(),
            exception.getMessage()
        );
    }
}
