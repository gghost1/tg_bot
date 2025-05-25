package ru.tg_bot.handler;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tg_bot.command.Command;
import ru.tg_bot.service.UserInfoService;

import java.util.concurrent.ConcurrentHashMap;

public class UpdateTextMessageDefaultStateNode extends ChainNode {
    private final ConcurrentHashMap<String, Command> commands;

    public UpdateTextMessageDefaultStateNode(ChainNode next, ConcurrentHashMap<String, Command> commands, UserInfoService userStateService) {
        super(next, userStateService);
        this.commands = commands;
    }

    public UpdateTextMessageDefaultStateNode(ConcurrentHashMap<String, Command> commands, UserInfoService userStateService) {
        super(userStateService);
        this.commands = commands;
    }

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && userStateService
                    .getState(String.valueOf(update.getMessage().getChatId()))
                    .isEmpty();
    }

    @Override
    public boolean process(Update update, DefaultAbsSender client) throws TelegramApiException {
        String text = update.getMessage().getText();
        if (commands.containsKey(text)) {
            commands.get(text).execute(update, client);
            return true;
        }
        return false;
    }
}
