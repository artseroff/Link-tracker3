package edu.java.client;

import edu.java.client.retry.RetryConfigRecord;

public record ClientConfigRecord(String baseUrl, RetryConfigRecord retry) {
}
