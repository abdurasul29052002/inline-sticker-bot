package com.example.inlinestickerbot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

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

    public InlineKeyboardMarkup getInlineKeyboard(int columnCount, String... texts) {
        int buttonCount = texts.length;
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (int i = 0; i < buttonCount; i++) {
            InlineKeyboardButton button = new InlineKeyboardButton(texts[i]);
            button.setCallbackData(texts[i]);
            row.add(button);
            if ((i + 1) % columnCount == 0 || (i + 1) == buttonCount) {
                rowList.add(row);
                row = new ArrayList<>();
            }
        }
        return new InlineKeyboardMarkup(rowList);
    }
}
