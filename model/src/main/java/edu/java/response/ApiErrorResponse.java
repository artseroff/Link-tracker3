package edu.java.response;

public record ApiErrorResponse(
    int code,
    String exceptionName,
    String message
) {

}
