package edu.java.scrapper.api.controller;

import edu.java.general.LinkSubscriptionDto;
import edu.java.response.ApiErrorResponse;
import edu.java.response.LinkResponse;
import edu.java.response.ListLinksResponse;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.exception.CorruptedLinkException;
import edu.java.scrapper.service.exception.EntityAlreadyExistException;
import edu.java.scrapper.service.exception.EntityNotFoundException;
import edu.java.scrapper.service.exception.NotSupportedLinkException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class LinksController {
    private final LinkService linkService;

    public LinksController(LinkService linkService) {
        this.linkService = linkService;
    }

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
    ResponseEntity<LinkResponse> add(@Valid @RequestBody LinkSubscriptionDto linkSubscriptionDto)
        throws EntityAlreadyExistException, EntityNotFoundException, NotSupportedLinkException, CorruptedLinkException {

        LinkResponse tracked = linkService.track(linkSubscriptionDto.chatId(), linkSubscriptionDto.url());
        return ResponseEntity.ok().body(tracked);
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
    ResponseEntity<LinkResponse> delete(
        @Valid @RequestBody LinkSubscriptionDto linkSubscriptionDto
    ) throws EntityNotFoundException {

        LinkResponse untracked = linkService.untrack(linkSubscriptionDto.chatId(), linkSubscriptionDto.url());
        return ResponseEntity.ok().body(untracked);
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
    ResponseEntity<ListLinksResponse> getLinks(
        @PathVariable("id") Long tgChatId
    ) throws EntityNotFoundException {

        Collection<LinkResponse> linkResponses = linkService.listAll(tgChatId);
        return ResponseEntity.ok(new ListLinksResponse((List<LinkResponse>) linkResponses));
    }
}
