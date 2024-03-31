package edu.java.client.retry;

import java.time.Duration;
import java.util.Set;

public record RetryConfigRecord(
    boolean enable,
    RetryType type,
    int maxAttempts,
    Duration delay,
    Set<Integer> codes
) {
}
