package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class BotApplicationTest {

    @MockBean
    private TelegramBot telegramBot;

}
