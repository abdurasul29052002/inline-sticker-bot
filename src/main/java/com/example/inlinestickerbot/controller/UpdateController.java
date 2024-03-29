package com.example.inlinestickerbot.controller;

import com.example.inlinestickerbot.config.BotConfig;
import com.example.inlinestickerbot.service.AdminService;
import com.example.inlinestickerbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

import java.util.ArrayList;

import static com.example.inlinestickerbot.InlineStickerBotApplication.admins;
import static com.example.inlinestickerbot.service.UserService.currentUser;

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
            currentUser = message.getFrom();
            if (message.hasText()) {
                if (admins.containsKey(message.getChatId())) {
                    if (message.getText().equals("/start")){
                        admins.put(message.getChatId(), "ADMIN");
                    }
                    if (admins.get(message.getChatId()).equals("ADMIN")) {
                        adminService.adminPanel(message.getText(), sendMessage);
                    }else {
                        userService.userPanel(message.getText(), sendMessage);
                    }
                } else {
                    userService.userPanel(message.getText(), sendMessage);
                }
            } else if (message.hasDocument()) {
                userService.userPanel(message.getDocument(), sendMessage);
            }
        } else if (update.hasInlineQuery()) {
            InlineQuery inlineQuery = update.getInlineQuery();
            AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery(inlineQuery.getId(), new ArrayList<>());
            currentUser = inlineQuery.getFrom();
            userService.userPanel(inlineQuery, answerInlineQuery);
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            SendMessage sendMessage = new SendMessage(callbackQuery.getMessage().getChatId().toString(), " ");
            currentUser = callbackQuery.getFrom();
            userService.userPanel(callbackQuery, sendMessage);
        }
    }
}
