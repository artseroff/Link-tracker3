package edu.java.scrapper.api.controller;

import edu.java.response.ApiErrorResponse;
import edu.java.scrapper.api.service.exception.EntityAlreadyExistException;
import edu.java.scrapper.api.service.exception.EntityNotFoundException;
import edu.java.scrapper.api.service.exception.NotSupportedLinkException;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ExceptionApiHandler {

    private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_HTTP_STATUS_MAP = Map.of(
        MethodArgumentTypeMismatchException.class, HttpStatus.BAD_REQUEST,
        NotSupportedLinkException.class, HttpStatus.BAD_REQUEST,
        EntityNotFoundException.class, HttpStatus.NOT_FOUND,
        EntityAlreadyExistException.class, HttpStatus.CONFLICT
    );

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidateException(MethodArgumentNotValidException exception) {
        String message = exception.getFieldErrors().stream()
            .map(fieldError -> "field:%s; error:%s".formatted(fieldError.getField(), fieldError.getDefaultMessage()))
            .collect(Collectors.joining(" "));
        return new ApiErrorResponse(
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            exception.getClass().getSimpleName(),
            message
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception exception) {
        HttpStatus status =
            EXCEPTION_HTTP_STATUS_MAP.getOrDefault(exception.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);

        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            status.getReasonPhrase(),
            exception.getClass().getSimpleName(),
            exception.getMessage()
        );
        return ResponseEntity.status(status)
            .body(apiErrorResponse);
    }

}
