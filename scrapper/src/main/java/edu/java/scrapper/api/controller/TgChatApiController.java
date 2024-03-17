package edu.java.scrapper.api.controller;

import edu.java.response.ApiErrorResponse;
import edu.java.scrapper.service.exception.EntityAlreadyExistException;
import edu.java.scrapper.service.exception.EntityNotFoundException;
import edu.java.scrapper.service.jdbc.SimpleTgChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/tg-chat/{id}")
@Slf4j
public class TgChatApiController {
    private final SimpleTgChatService chatService;

    public TgChatApiController(SimpleTgChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(summary = "Зарегистрировать чат")
    @ApiResponse(responseCode = "200", description = "Чат зарегистрирован")
    @ApiResponse(
        responseCode = "400",
        description = "Некорректные параметры запроса",
        content = @Content(mediaType = "application/json",
                           schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
        responseCode = "409",
        description = "Чат уже зарегистрирован",
        content = @Content(mediaType = "application/json",
                           schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
        responseCode = "500",
        description = "Ошибка сервера",
        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @PostMapping()
    public ResponseEntity<Void> create(@PathVariable("id") Long id) throws EntityAlreadyExistException {
        chatService.register(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить чат")
    @ApiResponse(responseCode = "200", description = "Чат успешно удалён")
    @ApiResponse(
        responseCode = "400",
        description = "Некорректные параметры запроса",
        content = @Content(mediaType = "application/json",
                           schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404",
                 description = "Чат не существует",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
        responseCode = "500",
        description = "Ошибка сервера",
        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @DeleteMapping()
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) throws EntityNotFoundException {
        chatService.unregister(id);
        return ResponseEntity.ok().build();
    }
}
