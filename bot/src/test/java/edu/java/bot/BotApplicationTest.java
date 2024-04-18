package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
public class BotApplicationTest {

    @MockBean
    private TelegramBot telegramBot;

    @Test
    private void enableSendingMessagesToChats() {
        doNothing().when(telegramBot).execute(any());
    }
}
