package edu.java.response;

public record ApiErrorResponse(
    String code,
    String exceptionName,
    String message
) {

}
