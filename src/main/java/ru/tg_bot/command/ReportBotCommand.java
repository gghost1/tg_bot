package ru.tg_bot.command;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tg_bot.service.ReportGeneratorService;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ReportBotCommand extends Command {
    private final ReportGeneratorService reportGeneratorService;

    public ReportBotCommand(ReportGeneratorService reportGeneratorService) {
        super("/report", "Get report in word format");
        this.reportGeneratorService = reportGeneratorService;
    }

    @Override
    public void execute(Update update, DefaultAbsSender client) throws TelegramApiException {
        try {
            byte[] reportBytes = reportGeneratorService.generateReport();
            InputFile document = new InputFile(
                    new ByteArrayInputStream(reportBytes),
                    "report.docx"
            );

            SendDocument sendDocument = SendDocument.builder()
                    .chatId(update.getMessage().getChatId())
                    .document(document)
                    .build();

            client.executeAsync(sendDocument);
        } catch (IOException e) {
            SendMessage sendMessage = SendMessage
                    .builder()
                    .chatId(update.getMessage().getChatId())
                    .text("Something went wrong. Please try again later.")
                    .build();
            client.execute(sendMessage);
            throw new TelegramApiException("Error generating report", e);
        }
    }
}
