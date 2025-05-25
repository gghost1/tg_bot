package ru.tg_bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BotConfig {

    @Value("${bot.token}")
    private String token;

    @Bean(name = "botToken")
    public String botToken() {
        return token;
    }

}
