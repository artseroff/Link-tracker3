package edu.java.scrapper.domain.dto;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class TimeDifferenceUtils {
    private static final Duration EPS_DURATION = Duration.of(1, ChronoUnit.MINUTES);

    private TimeDifferenceUtils() {
    }

    public static boolean isTimeEqualWithEpsilon(
        OffsetDateTime firstTime,
        OffsetDateTime secondTime,
        Duration epsilon
    ) {
        if (Objects.equals(firstTime, secondTime)) {
            return true;
        }
        if (firstTime == null) {
            return false;
        }
        if (secondTime == null) {
            return false;
        }
        return Duration.between(
                firstTime.toLocalTime(),
                secondTime.toLocalTime()
            ).abs()
            .compareTo(epsilon) <= 0;
    }

    public static boolean isTimeEqualWithEpsilon(OffsetDateTime firstTime, OffsetDateTime secondTime) {
        return isTimeEqualWithEpsilon(firstTime, secondTime, EPS_DURATION);
    }
}
