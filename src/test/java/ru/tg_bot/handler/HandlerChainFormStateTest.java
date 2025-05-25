package ru.tg_bot.handler;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tg_bot.exception.UnexpectedException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HandlerChainFormStateTest extends HandlerChainSettings {

    @Test
    void positiveTest() throws TelegramApiException {
        userInfoService.setState("1", "/form");
        userInfoService.setState("1_form", "name");

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn("Test");
        when(update.getMessage().getChatId()).thenReturn(1L);
        when(update.getMessage().getChat()).thenReturn(chat);
        when(update.getMessage().getChat().getFirstName()).thenReturn("Test");
        DefaultAbsSender client = mock(DefaultAbsSender.class);
        handlerConfig.handler().handle(update, client);

        assertTrue(userInfoService.getSubInfo("1", "name").isPresent());
        assertEquals("Test", userInfoService.getSubInfo("1", "name").get());
    }

    @Test
    void interruptionTest() throws TelegramApiException {
        userInfoService.setState("1", "/form");
        userInfoService.setState("1_form", "name");

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
        verify(client, times(1)).execute(any(SendMessage.class));

        assertTrue(userInfoService.getState("1").isEmpty());
        assertTrue(userInfoService.getState("1_form").isEmpty());
    }

    @Test
    void wrongEmailTest() throws TelegramApiException {
        userInfoService.setState("1", "/form");
        userInfoService.setState("1_form", "email");

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn("test");
        when(update.getMessage().getChatId()).thenReturn(1L);
        when(update.getMessage().getChat()).thenReturn(chat);
        when(update.getMessage().getChat().getFirstName()).thenReturn("Test");
        DefaultAbsSender client = mock(DefaultAbsSender.class);
        handlerConfig.handler().handle(update, client);

        assertTrue(userInfoService.getSubInfo("1", "email").isEmpty());
        verify(client, times(1)).execute(SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text("Invalid email format")
                .build());
    }

    @Test
    void wrongMarkValueTest() throws TelegramApiException {
        userInfoService.setState("1", "/form");
        userInfoService.setState("1_form", "mark");

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn("11");
        when(update.getMessage().getChatId()).thenReturn(1L);
        when(update.getMessage().getChat()).thenReturn(chat);
        when(update.getMessage().getChat().getFirstName()).thenReturn("Test");
        DefaultAbsSender client = mock(DefaultAbsSender.class);
        handlerConfig.handler().handle(update, client);

        assertTrue(userInfoService.getSubInfo("1", "mark").isEmpty());
        verify(client, times(1)).execute(SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text("Invalid mark value")
                .build());
    }

    @Test
    void wrongMarkFormatTest() throws TelegramApiException {
        userInfoService.setState("1", "/form");
        userInfoService.setState("1_form", "mark");

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn("test");
        when(update.getMessage().getChatId()).thenReturn(1L);
        when(update.getMessage().getChat()).thenReturn(chat);
        when(update.getMessage().getChat().getFirstName()).thenReturn("Test");
        DefaultAbsSender client = mock(DefaultAbsSender.class);
        handlerConfig.handler().handle(update, client);

        assertTrue(userInfoService.getSubInfo("1", "mark").isEmpty());
        verify(client, times(1)).execute(SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text("Invalid mark format")
                .build());
    }

    @Test
    void unexpectedErrorTest() throws TelegramApiException {
        userInfoService.setState("1", "/form");
        userInfoService.setState("1_form", "mark");

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn("5");
        when(update.getMessage().getChatId()).thenReturn(1L);
        when(update.getMessage().getChat()).thenReturn(chat);
        when(update.getMessage().getChat().getFirstName()).thenReturn("Test");
        DefaultAbsSender client = mock(DefaultAbsSender.class);
        assertThrows(UnexpectedException.class, () -> handlerConfig.handler().handle(update, client));
    }

}
