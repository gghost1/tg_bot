package ru.tg_bot;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.tg_bot.command.Command;
import ru.tg_bot.exception.UnexpectedException;
import ru.tg_bot.handler.ChainNode;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;


@Service
@Slf4j
public class BotCore extends TelegramLongPollingBot {
    private final ConcurrentHashMap<String, Command> commands;
    private final Executor botAsyncExecutor;
    private final ChainNode handler;

    public BotCore(String botToken, ChainNode handler, ConcurrentHashMap<String, Command> commands, Executor botAsyncExecutor) {
        super(botToken);
        this.commands = commands;
        this.handler = handler;
        this.botAsyncExecutor = botAsyncExecutor;
    }

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
            execute(new SetMyCommands(
                    commands.values()
                            .stream()
                            .map(command -> (BotCommand) command)
                            .toList(),
                    new BotCommandScopeDefault(), "en")
            );
            execute(new SetMyCommands(
                    commands.values()
                            .stream()
                            .map(command -> (BotCommand) command)
                            .toList(),
                    new BotCommandScopeDefault(), "ru")
            );
        } catch (TelegramApiException e) {
            log.error("Error registering commands: {}", e.getMessage());
        } catch (RuntimeException e) {
            log.error("Unknown error: {}", e.getMessage());
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        updates.forEach(update ->
                botAsyncExecutor.execute(
                        () -> onUpdateReceived(update)
                )
        );
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            handler.handle(update, this);
        } catch (TelegramApiException e) {
            log.error("Error handling update: {}", e.getMessage());
        } catch (UnexpectedException e) {
            log.error("Unexpected error: {}", e.getMessage());
        } catch (RuntimeException e) {
            log.error("Unknown error: {}", e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return "Telegram bot";
    }
}
