package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.client.scrapper.ScrapperClient;
import edu.java.general.ApiException;
import edu.java.general.LinkSubscriptionDto;
import edu.java.response.LinkResponse;
import edu.java.response.ListLinksResponse;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthorizedUserTest extends BotApplicationTest {
    private static final Long CHAT_ID = 1L;
    private static final String PARAMETER_CHAT_ID = "chat_id";
    private static final String PARAMETER_TEXT = "text";
    private static final Long LINK_ID = 1L;
    private static final URI SOF_URL = URI.create("https://stackoverflow.com/questions/214741");
    private static final String NEED_REGISTRATION = "Вы не зарегистрированы";

    private final BotController botController;

    @Mock
    private Update updateMock;

    @Mock
    private Message messageMock;

    @MockBean
    private ScrapperClient scrapperClient;

    @Autowired
    public AuthorizedUserTest(BotController botController) {
        this.botController = botController;
    }

    @BeforeEach public void setUpMocks() {
        when(updateMock.message()).thenReturn(messageMock);

        Chat chatMock = mock(Chat.class);
        when(messageMock.chat()).thenReturn(chatMock);
        when(chatMock.id()).thenReturn(CHAT_ID);
    }

    private void setInputMessageText(String text) {
        when(messageMock.text()).thenReturn(text);
    }

    @Test
    public void authorization_Ok() {
        // Arrange
        doNothing().when(scrapperClient).createChat(CHAT_ID);
        // Act
        setInputMessageText("/start");
        Map<String, Object> parameters = botController.handleUpdate(updateMock).getParameters();

        // Assert
        Assertions.assertEquals(CHAT_ID, parameters.get(PARAMETER_CHAT_ID));
        Assertions.assertEquals("Вы успешно зарегистрировались", parameters.get(PARAMETER_TEXT));
    }

    @Test
    public void authorization_InternalError() {
        // Arrange
        doThrow(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Скраппер упал"))
            .when(scrapperClient)
            .createChat(CHAT_ID);

        // Act
        setInputMessageText("/start");
        Map<String, Object> parameters = botController.handleUpdate(updateMock).getParameters();

        // Assert
        Assertions.assertEquals(CHAT_ID, parameters.get(PARAMETER_CHAT_ID));
        Assertions.assertEquals("Сервис отслеживания ссылок недоступен", parameters.get(PARAMETER_TEXT));
    }

    @Test
    public void authorization_AlreadyAuthorized() {
        // Arrange
        String chatAlreadyRegistered = "Вы уже зарегистрированы";
        doThrow(new ApiException(HttpStatus.CONFLICT.value(), chatAlreadyRegistered))
            .when(scrapperClient)
            .createChat(CHAT_ID);

        // Act
        setInputMessageText("/start");
        Map<String, Object> parameters = botController.handleUpdate(updateMock).getParameters();

        // Assert
        Assertions.assertEquals(CHAT_ID, parameters.get(PARAMETER_CHAT_ID));
        Assertions.assertEquals(chatAlreadyRegistered, parameters.get(PARAMETER_TEXT));
    }

    @Test
    public void list_NoTrackedLinks() {
        // Arrange
        ListLinksResponse listLinksResponse = new ListLinksResponse(List.of());
        when(scrapperClient.getLinks(CHAT_ID)).thenReturn(listLinksResponse);
        // Act
        setInputMessageText("/list");
        Map<String, Object> parameters = botController.handleUpdate(updateMock).getParameters();

        // Assert
        Assertions.assertEquals(CHAT_ID, parameters.get(PARAMETER_CHAT_ID));
        Assertions.assertEquals("Список отслеживаемых ссылок пуст", parameters.get(PARAMETER_TEXT));
    }

    @Test
    public void list_OneLink() {
        // Arrange
        ListLinksResponse listLinksResponse = new ListLinksResponse(
            List.of(new LinkResponse(LINK_ID, SOF_URL)));
        when(scrapperClient.getLinks(CHAT_ID)).thenReturn(listLinksResponse);

        // Act
        setInputMessageText("/list");
        Map<String, Object> parameters = botController.handleUpdate(updateMock).getParameters();

        // Assert
        Assertions.assertEquals(CHAT_ID, parameters.get(PARAMETER_CHAT_ID));
        Assertions.assertEquals(
            "Вы отслеживаете следующие ссылки:\n1) %s".formatted(SOF_URL),
            parameters.get(PARAMETER_TEXT)
        );
    }

    @Test
    public void list_NeedRegistration() {
        // Arrange
        doThrow(new ApiException(HttpStatus.NOT_FOUND.value(), NEED_REGISTRATION))
            .when(scrapperClient)
            .getLinks(CHAT_ID);

        // Act
        setInputMessageText("/list");
        Map<String, Object> parameters = botController.handleUpdate(updateMock).getParameters();

        // Assert
        Assertions.assertEquals(CHAT_ID, parameters.get(PARAMETER_CHAT_ID));
        Assertions.assertEquals(NEED_REGISTRATION, parameters.get(PARAMETER_TEXT));
    }

    @Test
    public void addLink_NeedRegistration() {
        // Arrange
        LinkSubscriptionDto linkSubscriptionDto = new LinkSubscriptionDto(CHAT_ID, SOF_URL);
        doThrow(new ApiException(HttpStatus.NOT_FOUND.value(), NEED_REGISTRATION))
            .when(scrapperClient)
            .addLink(linkSubscriptionDto);

        // Act
        setInputMessageText("/track %s".formatted(SOF_URL));
        Map<String, Object> parameters = botController.handleUpdate(updateMock).getParameters();

        // Assert
        Assertions.assertEquals(CHAT_ID, parameters.get(PARAMETER_CHAT_ID));
        Assertions.assertEquals(NEED_REGISTRATION, parameters.get(PARAMETER_TEXT));
    }

    @Test
    public void addLink_Ok() {
        // Arrange
        LinkSubscriptionDto linkSubscriptionDto = new LinkSubscriptionDto(CHAT_ID, SOF_URL);
        LinkResponse linkResponse = new LinkResponse(LINK_ID, SOF_URL);
        when(scrapperClient.addLink(linkSubscriptionDto)).thenReturn(linkResponse);

        // Act
        setInputMessageText("/track %s".formatted(SOF_URL));
        Map<String, Object> parameters = botController.handleUpdate(updateMock).getParameters();

        // Assert
        Assertions.assertEquals(CHAT_ID, parameters.get(PARAMETER_CHAT_ID));
        Assertions.assertEquals(
            "Ссылка %s добавлена в список отслеживаемых".formatted(SOF_URL),
            parameters.get(PARAMETER_TEXT)
        );
    }

    @Test
    public void addLink_AlreadyTracked() {
        // Arrange
        String alreadyTrackedLink = "Вы уже отслеживаете ссылку %s".formatted(SOF_URL);
        LinkSubscriptionDto linkSubscriptionDto = new LinkSubscriptionDto(CHAT_ID, SOF_URL);
        doThrow(new ApiException(HttpStatus.CONFLICT.value(), alreadyTrackedLink))
            .when(scrapperClient)
            .addLink(linkSubscriptionDto);

        // Act
        setInputMessageText("/track %s".formatted(SOF_URL));
        Map<String, Object> parameters = botController.handleUpdate(updateMock).getParameters();

        // Assert
        Assertions.assertEquals(CHAT_ID, parameters.get(PARAMETER_CHAT_ID));
        Assertions.assertEquals(
            alreadyTrackedLink,
            parameters.get(PARAMETER_TEXT)
        );
    }

    @Test
    public void deleteLink_NeedRegistration() {
        // Arrange
        LinkSubscriptionDto linkSubscriptionDto = new LinkSubscriptionDto(CHAT_ID, SOF_URL);
        doThrow(new ApiException(HttpStatus.NOT_FOUND.value(), NEED_REGISTRATION))
            .when(scrapperClient)
            .deleteLink(linkSubscriptionDto);

        // Act
        setInputMessageText("/untrack %s".formatted(SOF_URL));
        Map<String, Object> parameters = botController.handleUpdate(updateMock).getParameters();

        // Assert
        Assertions.assertEquals(CHAT_ID, parameters.get(PARAMETER_CHAT_ID));
        Assertions.assertEquals(NEED_REGISTRATION, parameters.get(PARAMETER_TEXT));
    }

    @Test
    public void deleteLink_NotTracked() {
        // Arrange
        String notTrackedLink = "Ссылка %s вами не отслеживается".formatted(SOF_URL);
        LinkSubscriptionDto linkSubscriptionDto = new LinkSubscriptionDto(CHAT_ID, SOF_URL);
        doThrow(new ApiException(HttpStatus.NOT_FOUND.value(), notTrackedLink))
            .when(scrapperClient)
            .deleteLink(linkSubscriptionDto);

        // Act
        setInputMessageText("/untrack %s".formatted(SOF_URL));
        Map<String, Object> parameters = botController.handleUpdate(updateMock).getParameters();

        // Assert
        Assertions.assertEquals(CHAT_ID, parameters.get(PARAMETER_CHAT_ID));
        Assertions.assertEquals(
            notTrackedLink,
            parameters.get(PARAMETER_TEXT)
        );
    }

    @Test
    public void deleteLink_Ok() {
        // Arrange
        LinkSubscriptionDto linkSubscriptionDto = new LinkSubscriptionDto(CHAT_ID, SOF_URL);
        LinkResponse linkResponse = new LinkResponse(LINK_ID, SOF_URL);
        when(scrapperClient.deleteLink(linkSubscriptionDto)).thenReturn(linkResponse);

        // Act
        setInputMessageText("/untrack %s".formatted(SOF_URL));
        Map<String, Object> parameters = botController.handleUpdate(updateMock).getParameters();

        // Assert
        Assertions.assertEquals(CHAT_ID, parameters.get(PARAMETER_CHAT_ID));
        Assertions.assertEquals(
            "Ссылка %s удалена из списка отслеживаемых".formatted(SOF_URL),
            parameters.get(PARAMETER_TEXT)
        );
    }
}
