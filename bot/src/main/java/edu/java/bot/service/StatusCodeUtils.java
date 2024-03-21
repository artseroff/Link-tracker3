package edu.java.bot.service;

import org.springframework.http.HttpStatusCode;

public class StatusCodeUtils {
    private StatusCodeUtils() {
    }

    public static boolean is5xxServerError(String code) {
        int parsed;
        try {
            parsed = Integer.parseInt(code);
        } catch (NumberFormatException e) {
            return true;
        }
        return HttpStatusCode.valueOf(parsed).is5xxServerError();
    }
}
