package com.example.inlinestickerbot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

@Service
public class KeyboardService {

    public ReplyKeyboardMarkup getReplyKeyboard(int columnCount, String... texts) {
        int buttonCount = texts.length;
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        for (int i = 0; i < buttonCount; i++) {
            keyboardRow.add(new KeyboardButton(texts[i]));
            if ((i + 1) % columnCount == 0 || (i + 1) == buttonCount) {
                keyboardRows.add(keyboardRow);
                keyboardRow = new KeyboardRow();
            }
        }
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public InlineKeyboardMarkup getInlineKeyboard(int columnCount, Map<String, String> buttons) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        Iterator<Map.Entry<String, String>> iterator = buttons.entrySet().iterator();
        int i=0;
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            InlineKeyboardButton button = new InlineKeyboardButton(entry.getValue());
            button.setCallbackData(entry.getKey());
            row.add(button);
            if ((i + 1) % columnCount == 0 || (i + 1) == buttons.size()) {
                rowList.add(row);
                row = new ArrayList<>();
            }
            i++;
        }
        return new InlineKeyboardMarkup(rowList);
    }

    public InlineKeyboardMarkup getBotLinkKeyboard(){
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton("Go to bot");
        button.setUrl("https://t.me/sticketjbot");
        row.add(button);
        rowList.add(row);
        return new InlineKeyboardMarkup(rowList);
    }
}
