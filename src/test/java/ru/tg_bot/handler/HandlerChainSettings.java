package ru.tg_bot.handler;

import org.junit.jupiter.api.BeforeEach;
import ru.tg_bot.config.HandlerConfig;
import ru.tg_bot.repository.FormRepository;
import ru.tg_bot.service.FormService;
import ru.tg_bot.service.ReportGeneratorService;
import ru.tg_bot.service.UserInfoService;

import static org.mockito.Mockito.mock;

public class HandlerChainSettings {

    protected HandlerConfig handlerConfig;
    protected UserInfoService userInfoService;

    @BeforeEach
    public void init() {
        FormRepository formRepository = mock(FormRepository.class);
        userInfoService = new UserInfoService();
        FormService formService = new FormService(formRepository);
        ReportGeneratorService reportGeneratorService = new ReportGeneratorService(formService);
        handlerConfig = new HandlerConfig(userInfoService, formService, reportGeneratorService);
    }

}
