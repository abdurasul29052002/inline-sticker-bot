package com.example.inlinestickerbot.component;

import com.example.inlinestickerbot.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Component
public class Sender extends DefaultAbsSender {

    private final BotConfig botConfig;

    protected Sender(DefaultBotOptions options, BotConfig botConfig) {
        super(options);
        this.botConfig = botConfig;
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}
