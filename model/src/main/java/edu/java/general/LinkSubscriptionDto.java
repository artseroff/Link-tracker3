package edu.java.general;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record LinkSubscriptionDto(
    @NotNull Long chatId,
    @NotNull URI url) {
}
