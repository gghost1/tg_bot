package ru.tg_bot.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tg_bot.command.Command;
import ru.tg_bot.command.FormBotCommand;
import ru.tg_bot.command.ReportBotCommand;
import ru.tg_bot.command.StartBotCommand;
import ru.tg_bot.handler.ChainNode;
import ru.tg_bot.handler.UpdateTextMessageDefaultStateNode;
import ru.tg_bot.handler.UpdateTextMessageFormStateNode;
import ru.tg_bot.service.FormService;
import ru.tg_bot.service.ReportGeneratorService;
import ru.tg_bot.service.UserInfoService;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
@RequiredArgsConstructor
public class HandlerConfig {
    private final UserInfoService userStateService;
    private final FormService formService;
    private final ReportGeneratorService reportGeneratorService;

    @Bean
    public ChainNode handler() {
        return new UpdateTextMessageFormStateNode(commands(), userStateService)
                .setNext(new UpdateTextMessageDefaultStateNode(commands(), userStateService));
    }

    @Bean
    public ConcurrentHashMap<String, Command> commands() {
        ConcurrentHashMap<String, Command> commands = new ConcurrentHashMap<>();
        Command startBotCommand = new StartBotCommand();
        Command formBotCommand = new FormBotCommand(userStateService, formService);
        Command reportBotCommand = new ReportBotCommand(reportGeneratorService);
        commands.put(startBotCommand.getCommand(), startBotCommand);
        commands.put(formBotCommand.getCommand(), formBotCommand);
        commands.put(reportBotCommand.getCommand(), reportBotCommand);
        return commands;
    }

}
