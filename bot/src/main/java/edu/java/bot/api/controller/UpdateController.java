package edu.java.bot.api.controller;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.BotController;
import edu.java.request.LinkUpdateRequest;
import edu.java.response.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@Validated
@RequestMapping("/update")
@Slf4j
public class UpdateController {
    private final BotController botController;

    public UpdateController(BotController botController) {
        this.botController = botController;
    }

    @Operation(summary = "Отправить обновление")
    @ApiResponse(responseCode = "200", description = "Обновление обработано")
    @ApiResponse(
        responseCode = "400",
        description = "Некорректные параметры запроса",
        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "500",
        description = "Ошибка сервера",
        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @PostMapping()
    public ResponseEntity<Void> processUpdate(@Valid @RequestBody LinkUpdateRequest request) {
        List<Long> chatIds = request.tgChatIds();
        for (Long chatId : chatIds) {
            SendMessage message = new SendMessage(chatId, request.description());
            botController.sendMessage(message);
        }
        return ResponseEntity.ok().build();
    }
}
