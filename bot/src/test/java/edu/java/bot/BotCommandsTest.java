package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.command.HelpCommand;
import edu.java.bot.service.UserService;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = {BotApplication.class})
class BotCommandsTest {
    /*private static final Long CHAT_ID = 1L;

    private static final String PARAMETER_CHAT_ID = "chat_id";

    private static final String PARAMETER_TEXT = "text";

    private final BotController botController;

    private final UserService userService;

    private final HelpCommand helpCommand;

    @Mock
    private Update updateMock;

    @Mock
    private Message messageMock;

    @Autowired
    public BotCommandsTest(BotController botController, UserService userService, HelpCommand helpCommand) {
        this.botController = botController;
        this.userService = userService;
        this.helpCommand = helpCommand;
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

    @ParameterizedTest
    @MethodSource("unregisteredUserTestParameters")
    public void unregisteredUserTest(String expectedText, String inputText) {
        // Arrange

        // Act
        setInputMessageText(inputText);
        Map<String, Object> parameters = botController.handleUpdate(updateMock).getParameters();

        // Assert
        Assertions.assertEquals(CHAT_ID, parameters.get(PARAMETER_CHAT_ID));
        Assertions.assertEquals(expectedText, parameters.get(PARAMETER_TEXT));
    }

    private Arguments[] unregisteredUserTestParameters() {
        return new Arguments[] {
            Arguments.of("Передана пустая команда", ""),
            Arguments.of("Команда должна начинаться с '/'", "Привет"),
            Arguments.of("Введена неизвестная команда", "/Привет"),
            Arguments.of("Вы не зарегистрированы", "/list"),
            Arguments.of("Команда /list не имеет параметров", "/list маленький"),
            Arguments.of(buildExpectedHelpText(), "/help"),
            Arguments.of("Команда /track принимает один обязательный параметр - ссылку", "/track"),
            Arguments.of("Введена ссылка, которая не поддерживается для отслеживания", "/track https://ya.ru"),
            Arguments.of("Введена ссылка, которая не поддерживается для отслеживания", "/untrack https://yary"),
            Arguments.of("Протокол ссылки не указан", "/track ya.ru"),
        };
    }

    private String buildExpectedHelpText() {
        try {
            Method method = helpCommand.getClass().getDeclaredMethod("buildHelpText");
            method.setAccessible(true);

            return (String) method.invoke(helpCommand);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void authorizationTest() {
        // Arrange

        // Act
        setInputMessageText("/start");
        Map<String, Object> parameters = botController.handleUpdate(updateMock).getParameters();

        // Assert
        Assertions.assertEquals(CHAT_ID, parameters.get(PARAMETER_CHAT_ID));
        Assertions.assertEquals("Вы успешно зарегистрировались", parameters.get(PARAMETER_TEXT));
        userService.clear();
    }

    @ParameterizedTest
    @MethodSource("authorizedUserTestParameters")
    public void authorizedUserTest(String[] expectedTexts, String[] inputTexts) {
        // Arrange
        setInputMessageText("/start");
        botController.handleUpdate(updateMock).getParameters();

        for (int i = 0; i < expectedTexts.length; i++) {
            // Act
            setInputMessageText(inputTexts[i]);
            Map<String, Object> parameters = botController.handleUpdate(updateMock).getParameters();

            //Assert
            Assertions.assertEquals(CHAT_ID, parameters.get(PARAMETER_CHAT_ID));
            Assertions.assertEquals(expectedTexts[i], parameters.get(PARAMETER_TEXT));
        }
        userService.clear();
    }

    private static Arguments[] authorizedUserTestParameters() {
        return new Arguments[] {
            Arguments.of(new String[] {"Вы уже зарегистрированы"}, new String[] {"/start"}),
            Arguments.of(new String[] {"Список отслеживаемых ссылок пуст"}, new String[] {"/list"}),
            Arguments.of(
                new String[] {
                    "Указанной ссылки нет в списке отслеживаемых",
                    "Ссылка https://github.com добавлена в список отслеживаемых",
                    "Указанная ссылка уже отслеживается",
                    "Ссылка https://stackoverflow.com добавлена в список отслеживаемых",
                    "Ссылка https://github.com удалена из списка отслеживаемых",
                    """
                       Вы отслеживаете следующие ссылки:
                       1) https://stackoverflow.com"""
                },
                new String[] {
                    "/untrack https://github.com",
                    "/track https://github.com",
                    "/track https://github.com",
                    "/track https://stackoverflow.com",
                    "/untrack https://github.com",
                    "/list"
                }
            )
        };
    }*/

}
