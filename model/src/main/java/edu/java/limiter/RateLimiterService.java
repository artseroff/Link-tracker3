package edu.java.limiter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiterService {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private final Bandwidth bandwidth;

    public RateLimiterService(Bandwidth bandwidth) {
        this.bandwidth = bandwidth;
    }

    public Bucket resolveBucket(String ipAddress) {
        return cache.computeIfAbsent(ipAddress, this::newBucket);
    }

    private Bucket newBucket(String ipAddress) {
        return Bucket.builder()
            .addLimit(bandwidth)
            .build();
    }

}
