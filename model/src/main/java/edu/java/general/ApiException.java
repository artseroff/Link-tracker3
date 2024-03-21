package edu.java.general;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final String code;

    public ApiException(String code, String message) {
        super(message);
        this.code = code;
    }
}
