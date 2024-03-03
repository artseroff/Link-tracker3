package edu.java.scrapper.api.controller;

import edu.java.general.LinkDto;
import edu.java.response.ApiErrorResponse;
import edu.java.response.LinkResponse;
import edu.java.response.ListLinksResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/links")
public class LinksController {
    @Operation(summary = "Добавить отслеживание ссылки")
    @ApiResponse(
        responseCode = "200",
        description = "Ссылка успешно добавлена",
        content = @Content(mediaType = "application/json",
                           schema = @Schema(implementation = LinkResponse.class)))
    @ApiResponse(
        responseCode = "400",
        description = "Некорректные параметры запроса",
        content = @Content(mediaType = "application/json",
                           schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
        responseCode = "409",
        description = "Ссылка уже отслеживается",
        content = @Content(mediaType = "application/json",
                           schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
        responseCode = "500",
        description = "Ошибка сервера",
        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping
    ResponseEntity<LinkResponse> add(@Valid @RequestBody LinkDto linkDto) {
        return ResponseEntity.ok(new LinkResponse(linkDto.chatId(), linkDto.url()));
    }

    @Operation(summary = "Убрать отслеживание ссылки")
    @ApiResponse(
        responseCode = "200",
        description = "Ссылка успешно убрана",
        content = @Content(mediaType = "application/json",
                           schema = @Schema(implementation = LinkResponse.class)))
    @ApiResponse(
        responseCode = "400",
        description = "Некорректные параметры запроса",
        content = @Content(mediaType = "application/json",
                           schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
        responseCode = "404",
        description = "Ссылка не найдена",
        content = @Content(mediaType = "application/json",
                           schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
        responseCode = "500",
        description = "Ошибка сервера",
        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @DeleteMapping
    ResponseEntity<LinkResponse> linksDelete(
        @Valid @RequestBody LinkDto linkDto
    ) {
        return ResponseEntity.ok(new LinkResponse(linkDto.chatId(), linkDto.url()));
    }

    @Operation(summary = "Получить все отслеживаемые ссылки")
    @ApiResponse(
        responseCode = "200",
        description = "Ссылки успешно получены",
        content = @Content(mediaType = "application/json",
                           schema = @Schema(implementation = ListLinksResponse.class)))
    @ApiResponse(
        responseCode = "400",
        description = "Некорректные параметры запроса",
        content = @Content(mediaType = "application/json",
                           schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(
        responseCode = "500",
        description = "Ошибка сервера",
        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @GetMapping("/{id}")
    ResponseEntity<ListLinksResponse> linksGet(
        @PathVariable("id") Long tgChatId
    ) {
        try {
            return ResponseEntity.ok(new ListLinksResponse(List.of(new LinkResponse(1L, new URI("ya.ru")))));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
