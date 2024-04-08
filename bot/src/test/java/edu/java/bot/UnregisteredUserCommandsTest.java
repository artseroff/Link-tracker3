package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.command.HelpCommand;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UnregisteredUserCommandsTest extends BotApplicationTest {
    private static final Long CHAT_ID = 1L;

    private static final String PARAMETER_CHAT_ID = "chat_id";

    private static final String PARAMETER_TEXT = "text";

    private final BotController botController;

    private final HelpCommand helpCommand;

    @Mock
    private Update updateMock;

    @Mock
    private Message messageMock;

    @Autowired
    public UnregisteredUserCommandsTest(BotController botController, HelpCommand helpCommand) {
        this.botController = botController;
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
            Arguments.of("Команда /list не имеет параметров", "/list маленький"),
            Arguments.of(buildExpectedHelpText(), "/help"),
            Arguments.of("Команда /track принимает один обязательный параметр - ссылку", "/track"),
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

}
