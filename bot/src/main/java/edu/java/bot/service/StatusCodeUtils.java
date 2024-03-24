package edu.java.bot.service;

import org.springframework.http.HttpStatusCode;

public class StatusCodeUtils {
    private StatusCodeUtils() {
    }

    public static boolean is5xxServerError(int code) {
        return HttpStatusCode.valueOf(code).is5xxServerError();
    }
}
