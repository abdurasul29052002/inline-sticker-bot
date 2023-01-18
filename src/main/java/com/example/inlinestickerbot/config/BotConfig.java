package com.example.inlinestickerbot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yml")
@Data
public class BotConfig {

    @Value("${username}")
    private String username;

    @Value("${token}")
    private String token;
}
