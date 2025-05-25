package ru.tg_bot.handler;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tg_bot.command.Command;
import ru.tg_bot.service.UserInfoService;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UpdateTextMessageFormStateNode extends ChainNode {
    private final ConcurrentHashMap<String, Command> commands;

    public UpdateTextMessageFormStateNode(ChainNode next, ConcurrentHashMap<String, Command> commands, UserInfoService userStateService) {
        super(next, userStateService);
        this.commands = commands;
    }

    public UpdateTextMessageFormStateNode(ConcurrentHashMap<String, Command> commands, UserInfoService userStateService) {
        super(userStateService);
        this.commands = commands;
    }

    @Override
    public boolean canHandle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Optional<String> state = userStateService.getState(String.valueOf(update.getMessage().getChatId()));
            if (state.isPresent()) {
                return state.get().equals("/form");
            }
        }
        return false;
    }

    @Override
    public boolean process(Update update, DefaultAbsSender client) throws TelegramApiException {
        String text = update.getMessage().getText();
        if (commands.containsKey(text)) {
            userStateService.removeState(String.valueOf(update.getMessage().getChatId()));
            userStateService.removeState(update.getMessage().getChatId() + "_form");
            commands.get(text).execute(update, client);
            return true;
        }
        commands.get("/form").execute(update, client);
        return false;
    }
}
