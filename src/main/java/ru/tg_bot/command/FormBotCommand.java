package ru.tg_bot.command;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tg_bot.exception.UnexpectedException;
import ru.tg_bot.model.UserEmail;
import ru.tg_bot.service.FormService;
import ru.tg_bot.service.UserInfoService;

import java.util.Optional;

public class FormBotCommand extends Command {
    private final UserInfoService userInfoService;
    private final FormService formService;

    public FormBotCommand(UserInfoService userInfoService, FormService formService) {
        super("/form", "Start sequence of questions");
        this.userInfoService = userInfoService;
        this.formService = formService;
    }

    @Override
    public void execute(Update update, DefaultAbsSender client) throws TelegramApiException {
        Optional<String> subState = userInfoService.getState(update.getMessage().getChatId() + "_form");
        if (subState.isPresent()) {
            switch (subState.get()) {
                case "name" -> nameSubState(update, client);
                case "email" -> emailSubState(update, client);
                case "mark" -> markSubState(update, client);
                default -> error(update, client);
            }
        } else {
            initSubState(update, client);
        }
    }

    private void initSubState(Update update, DefaultAbsSender client) throws TelegramApiException {
        SendMessage sendMessage = SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text("Enter your name")
                .build();
        client.execute(sendMessage);
        userInfoService.setState(String.valueOf(update.getMessage().getChatId()), "/form");
        userInfoService.setState(update.getMessage().getChatId() + "_form", "name");
    }

    private void nameSubState(Update update, DefaultAbsSender client) throws TelegramApiException {
        String name = update.getMessage().getText();
        userInfoService.setSubInfo(String.valueOf(update.getMessage().getChatId()), "name", name);
        SendMessage sendMessage = SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text("Enter your email")
                .build();
        client.execute(sendMessage);
        userInfoService.setState(update.getMessage().getChatId() + "_form", "email");
    }

    private void emailSubState(Update update, DefaultAbsSender client) throws TelegramApiException {
        UserEmail email;
        try {
            email = UserEmail.of(update.getMessage().getText());
        } catch (IllegalArgumentException e) {
            SendMessage sendMessage = SendMessage
                    .builder()
                    .chatId(update.getMessage().getChatId())
                    .text("Invalid email format")
                    .build();
            client.execute(sendMessage);
            return;
        }

        userInfoService.setSubInfo(String.valueOf(update.getMessage().getChatId()), "email", email.getValue());
        SendMessage sendMessage = SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text("Enter your mark (1 - 10)")
                .build();
        client.execute(sendMessage);
        userInfoService.setState(update.getMessage().getChatId() + "_form", "mark");
    }

    private void markSubState(Update update, DefaultAbsSender client) throws TelegramApiException {
        String mark = update.getMessage().getText();
        int markInt;
        try {
            markInt = Integer.parseInt(mark);
        } catch (NumberFormatException e) {
            SendMessage sendMessage = SendMessage
                    .builder()
                    .chatId(update.getMessage().getChatId())
                    .text("Invalid mark format")
                    .build();
            client.execute(sendMessage);
            return;
        }
        if (markInt < 1 || markInt > 10) {
            SendMessage sendMessage = SendMessage
                    .builder()
                    .chatId(update.getMessage().getChatId())
                    .text("Invalid mark value")
                    .build();
            client.execute(sendMessage);
            return;
        }
        Optional<String> name = userInfoService.getSubInfo(String.valueOf(update.getMessage().getChatId()), "name");
        Optional<String> email = userInfoService.getSubInfo(String.valueOf(update.getMessage().getChatId()), "email");
        if (name.isPresent() && email.isPresent()) {
            try {
                formService.create(name.get(), email.get(), Integer.parseInt(mark));
            } catch (Exception e) {
                error(update, client);
                return;
            }

            client.execute(SendMessage
                    .builder()
                    .chatId(update.getMessage().getChatId())
                    .text("Name: " + name.get() + "\nEmail: " + email.get() + "\nMark: " + mark)
                    .build());
            client.execute(SendMessage
                    .builder()
                    .chatId(update.getMessage().getChatId())
                    .text("Your answers saved")
                    .build());
            userInfoService.removeState(String.valueOf(update.getMessage().getChatId()));
            userInfoService.removeState(update.getMessage().getChatId() + "_form");
        } else {
            SendMessage sendMessage = SendMessage
                    .builder()
                    .chatId(update.getMessage().getChatId())
                    .text("Something went wrong. Please try again later")
                    .build();
            client.execute(sendMessage);
            userInfoService.removeState(String.valueOf(update.getMessage().getChatId()));
            userInfoService.removeState(update.getMessage().getChatId() + "_form");
            throw new UnexpectedException("Some form information is missing");
        }
    }

    private void error(Update update, DefaultAbsSender client) throws TelegramApiException {
        SendMessage sendMessage = SendMessage
                .builder()
                .chatId(update.getMessage().getChatId())
                .text("Something went wrong")
                .build();
        client.execute(sendMessage);
        initSubState(update, client);
    }

}
