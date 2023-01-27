package com.example.inlinestickerbot.service;

import com.example.inlinestickerbot.component.Sender;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.print.DocFlavor;
import java.io.File;
import java.io.StringWriter;

import static com.example.inlinestickerbot.InlineStickerBotApplication.*;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final KeyboardService keyboardService;
    private final Sender sender;
    private final ImageService imageService;

    public void greetingMessage(SendMessage sendMessage){
        sendMessage.setText("Bu bot sizga matn asosida sticker yasab berish uchun xizmat qiladi");
        sendMessage.setReplyMarkup(keyboardService.getReplyKeyboard(1, "Fontlarni o`zgartirish"));
    }

    @SneakyThrows
    public void editFontsMessage(SendMessage sendMessage) {
        sendMessage.setText("Sizda hozircha quyidagicha fontlar mavjud bo`lib siz ularni o`zgartirishingiz mumkin.\nServerning kuhcsizligi sabab bot faqat 2 ta font bilan ishlay oladi");
        sendMessage.setReplyMarkup(keyboardService.getReplyKeyboard(1, "Qo`shish"));
        sender.execute(sendMessage);
    }

    public void addFontMessage(SendMessage sendMessage) {
        sendMessage.setText("Qo`shmoqchi bo`lgan font fileni tashlang\n\nFont file .ttf formatida bo`ladi");
    }
}
