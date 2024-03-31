package edu.java.client.retry;

import edu.java.general.ApiException;
import java.time.Duration;
import java.util.Set;
import java.util.function.Predicate;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

public class WebClientRetryUtils {
    private static final Set<Integer> DEFAULT_RETRY_ON_CODES = Set.of(
        429, 500, 501, 502, 503, 504, 505, 506, 507, 508, 509, 510, 511
    );

    private WebClientRetryUtils() {
    }

    public static Retry buildRetry(RetryConfigRecord retryConfigRecord) {
        if (retryConfigRecord == null || !retryConfigRecord.enable()) {
            return Retry.max(0)
                .filter(buildFilter(DEFAULT_RETRY_ON_CODES));
        }
        return switch (retryConfigRecord.type()) {
            case CONSTANT -> buildConstantRetry(retryConfigRecord);
            case LINEAR -> buildLinearRetry(retryConfigRecord);
            case EXPONENTIAL -> buildExponentialRetry(retryConfigRecord);
        };
    }

    private static Retry buildConstantRetry(RetryConfigRecord retryConfigRecord) {
        return Retry.fixedDelay(retryConfigRecord.maxAttempts(), retryConfigRecord.delay())
            .filter(buildFilter(retryConfigRecord));
    }

    private static Retry buildLinearRetry(RetryConfigRecord retryConfigRecord) {
        Predicate<? super Throwable> filter = buildFilter(retryConfigRecord);
        return Retry.from(signal -> Flux.deferContextual(
                cv -> signal.contextWrite(cv)
                    .concatMap(retryWhenState -> {
                        Retry.RetrySignal copy = retryWhenState.copy();
                        Throwable currentFailure = copy.failure();
                        long iteration = copy.totalRetries();

                        if (currentFailure == null) {
                            return Mono.error(
                                new IllegalStateException("Retry.RetrySignal#failure() not expected to be null")
                            );
                        }
                        if (!filter.test(currentFailure)) {
                            return Mono.error(currentFailure);
                        }
                        if (iteration >= retryConfigRecord.maxAttempts()) {
                            return Mono.error(Exceptions.retryExhausted(
                                "Exhausted %d retries".formatted(iteration),
                                currentFailure
                            ));
                        }
                        Duration nextBackoff = retryConfigRecord.delay().multipliedBy(iteration);
                        return Mono.delay(nextBackoff, Schedulers.parallel());
                    })
            )
        );
    }

    private static Retry buildExponentialRetry(RetryConfigRecord retryConfigRecord) {
        return Retry.backoff(retryConfigRecord.maxAttempts(), retryConfigRecord.delay())
            .filter(buildFilter(retryConfigRecord));
    }

    private static Predicate<? super Throwable> buildFilter(Set<Integer> codes) {
        final Set<Integer> finalCodes = codes == null ? DEFAULT_RETRY_ON_CODES : codes;
        return throwable ->
            throwable instanceof ApiException
                && finalCodes
                .contains(((ApiException) throwable).getCode());
    }

    private static Predicate<? super Throwable> buildFilter(RetryConfigRecord retryConfigRecord) {
        return buildFilter(retryConfigRecord.codes());
    }
}
