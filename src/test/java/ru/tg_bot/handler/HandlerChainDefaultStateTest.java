package ru.tg_bot.handler;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HandlerChainDefaultStateTest extends HandlerChainSettings {

    @Test
    void positiveTest() throws TelegramApiException {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn("/start");
        when(update.getMessage().getChatId()).thenReturn(1L);
        when(update.getMessage().getChat()).thenReturn(chat);
        when(update.getMessage().getChat().getFirstName()).thenReturn("Test");
        DefaultAbsSender client = mock(DefaultAbsSender.class);
        handlerConfig.handler().handle(update, client);
        verify(client, times(1)).execute(SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text("Hello, " + update.getMessage().getChat().getFirstName())
                .build()
        );
    }

    @Test
    void negativeTest() throws TelegramApiException {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn("/test");
        when(update.getMessage().getChatId()).thenReturn(1L);
        when(update.getMessage().getChat()).thenReturn(chat);
        when(update.getMessage().getChat().getFirstName()).thenReturn("Test");
        DefaultAbsSender client = mock(DefaultAbsSender.class);
        handlerConfig.handler().handle(update, client);
        verify(client, times(0)).execute(any(SendMessage.class));
    }



}
