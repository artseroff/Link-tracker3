package edu.java.general;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record LinkDto(
    @NotNull Long chatId,
    @NotNull URI url) {
}
