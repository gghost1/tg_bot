package ru.tg_bot.command;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StartBotCommand extends Command {

    public StartBotCommand() {
        super("/start", "Start the bot");
    }

    @Override
    public void execute(Update update, DefaultAbsSender client) throws TelegramApiException {
        SendMessage sendMessage = SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text("Hello, " + update.getMessage().getChat().getFirstName())
                .build();
        client.execute(sendMessage);
    }

}
