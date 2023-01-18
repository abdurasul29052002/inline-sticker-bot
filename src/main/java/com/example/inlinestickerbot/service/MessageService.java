package com.example.inlinestickerbot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class MessageService {
    public void greetingMessage(SendMessage sendMessage){
        sendMessage.setText("Bu bot sizga matn asosida sticker yasab berish uchun xizmat qiladi");
    }
}
