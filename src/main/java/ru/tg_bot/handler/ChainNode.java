package ru.tg_bot.handler;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tg_bot.service.UserInfoService;

public abstract class ChainNode {
    private ChainNode next;
    protected UserInfoService userStateService;

    public ChainNode(ChainNode next, UserInfoService userStateService) {
        this.next = next;
        this.userStateService = userStateService;
    }

    public ChainNode(UserInfoService userStateService) {
        this.userStateService = userStateService;
    }

    public final void handle(Update update, DefaultAbsSender client) throws TelegramApiException {
        boolean done = false;
        if (canHandle(update)) {
            done = process(update, client);
        }
        if (done) return;
        next(update, client);
    }

    protected abstract boolean canHandle(Update update);

    protected abstract boolean process(Update update, DefaultAbsSender client) throws TelegramApiException;

    private void next(Update update, DefaultAbsSender client) throws TelegramApiException {
        if (next != null) {
            next.handle(update, client);
        }
    }

    public final ChainNode setNext(ChainNode next) {
        this.next = next;
        return this;
    }

}
