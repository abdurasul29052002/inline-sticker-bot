package com.example.inlinestickerbot.service;

import com.example.inlinestickerbot.component.Sender;
import com.example.inlinestickerbot.entity.User;
import com.example.inlinestickerbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.cached.InlineQueryResultCachedSticker;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.inlinestickerbot.InlineStickerBotApplication.*;
import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MessageService messageService;
    private final Sender sender;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final KeyboardService keyboardService;
    public static org.telegram.telegrambots.meta.api.objects.User currentUser;

    @SneakyThrows
    public void userPanel(String text, SendMessage sendMessage) {
        Long chatId = Long.valueOf(sendMessage.getChatId());
        if (!userRepository.existsByChatId(chatId)) {
            userRepository.save(new User(null, chatId, currentUser.getUserName(), true));
            imageService.createFolder(chatId);
            userCount++;
        }
        switch (text) {
            case "/start" -> {
                messageService.greetingMessage(sendMessage);
            }
            case "Fontlarni o`zgartirish" -> {
                messageService.editFontsMessage(sendMessage);
                List<File> files = imageService.generateImage("Hello", chatId.toString());
                SendPhoto sendPhoto = new SendPhoto(chatId.toString(), new InputFile());
                for (File file : files) {
                    Map<String, String> buttons = new HashMap<>();
                    buttons.put("O`chirish " + file.getName(), "O`chirish");
                    sendPhoto.setPhoto(new InputFile(file));
                    sendPhoto.setReplyMarkup(keyboardService.getInlineKeyboard(1,buttons));
                    sender.execute(sendPhoto);
                }
                return;
            }
            case "Qo`shish" -> {
                messageService.addFontMessage(sendMessage);
            }
        }
        sender.execute(sendMessage);
    }

    @SneakyThrows
    public void userPanel(InlineQuery inlineQuery, AnswerInlineQuery answerInlineQuery) {
        if (inlineQuery.getQuery().length() < 5)
            return;
        imageService.initFonts(inlineQuery.getFrom().getId());
        List<File> files = imageService.generateImage(inlineQuery.getQuery(), inlineQuery.getId());
        List<InlineQueryResult> results = answerInlineQuery.getResults();
        for (File file : files) {
            SendSticker sendSticker = new SendSticker("-1001478286553", new InputFile(file));
            Message executedMessage = sender.execute(sendSticker);
            stickerCount++;
            Sticker sticker = executedMessage.getSticker();
            InlineQueryResultCachedSticker inlineQueryResultCachedSticker = new InlineQueryResultCachedSticker(randomUUID().toString(), sticker.getFileId());
            results.add(inlineQueryResultCachedSticker);
            Thread.sleep(1500);
        }
        answerInlineQuery.setResults(results);
        sender.execute(answerInlineQuery);
        queryCount++;
        imageService.deleteFiles(inlineQuery.getId());
    }

    public void userPanel(Document document, SendMessage sendMessage) {

    }

    public void userPanel(CallbackQuery callbackQuery, SendMessage sendMessage) {

    }
}
