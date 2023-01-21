package com.example.inlinestickerbot.controller;

import com.example.inlinestickerbot.config.BotConfig;
import com.example.inlinestickerbot.service.AdminService;
import com.example.inlinestickerbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class UpdateController extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    private final BotConfig botConfig;
    private final UserService userService;
    private final AdminService adminService;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            SendMessage sendMessage = new SendMessage(message.getChatId().toString(), " ");
            UserService.currentUser = message.getFrom();
            if (message.hasText()) {
                if (message.getChatId() == 1324394249) {
                    adminService.adminPanel(message.getText(), sendMessage);
                } else {
                    userService.userPanel(message.getText(), sendMessage);
                }
            }
        } else if (update.hasInlineQuery()) {
            InlineQuery inlineQuery = update.getInlineQuery();
            AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery(inlineQuery.getId(), new ArrayList<>());
            userService.userPanel(inlineQuery, answerInlineQuery);
        }
    }
}
