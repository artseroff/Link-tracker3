package edu.java.client.retry;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Set;

public record RetryConfigRecord(
    boolean enable,
    @NotNull RetryType type,
    int maxAttempts,
    @NotNull Duration delay,
    Set<Integer> codes
) {
}
