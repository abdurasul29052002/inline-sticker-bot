package com.example.inlinestickerbot.service;

import com.example.inlinestickerbot.component.Sender;
import com.example.inlinestickerbot.entity.Font;
import com.example.inlinestickerbot.entity.User;
import com.example.inlinestickerbot.repository.FontRepository;
import com.example.inlinestickerbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.cached.InlineQueryResultCachedSticker;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static com.example.inlinestickerbot.InlineStickerBotApplication.*;
import static com.example.inlinestickerbot.service.ImageService.fontNames;
import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MessageService messageService;
    private final Sender sender;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final KeyboardService keyboardService;
    private final FontRepository fontRepository;
    public static org.telegram.telegrambots.meta.api.objects.User currentUser;
    private boolean broke = false;

    @SneakyThrows
    public void userPanel(String text, SendMessage sendMessage) {
        Long chatId = Long.valueOf(sendMessage.getChatId());
        if (!userRepository.existsByChatId(chatId)) {
            User user = new User(
                    null,
                    chatId,
                    currentUser.getUserName(),
                    true,
                    new ArrayList<>()
            );
            Font font = new Font(
                    null,
                    fontNames.get(0),
                    user
            );
            user.getFonts().add(font);
            userRepository.save(user);
            userCount++;
        }
        User user = userRepository.findByChatId(chatId).orElseThrow();
        switch (text) {
            case "/start" -> {
                messageService.greetingMessage(sendMessage);
            }
            case "Fontlarni o`zgartirish\uD83D\uDD24" -> {
                String[] usersFontNames = getUsersFontNames(user);
                SendPhoto sendPhoto = new SendPhoto(chatId.toString(), new InputFile());
                if (usersFontNames.length > 0) {
                    messageService.editFontsMessage(sendMessage);
                    List<File> files = imageService.generateImage("Hello", chatId.toString(), usersFontNames);
                    for (int i = 0; i < usersFontNames.length; i++) {
                        Map<String, String> buttons = new HashMap<>();
                        buttons.put("O`chirish" + usersFontNames[i], "O`chirish❌");
                        sendPhoto.setPhoto(new InputFile(files.get(i)));
                        sendPhoto.setCaption(usersFontNames[i]);
                        sendPhoto.setReplyMarkup(keyboardService.getInlineKeyboard(1, buttons));
                        sender.execute(sendPhoto);
                        Thread.sleep(1000);
                    }
                } else {
                    messageService.userWithoutFontsMessage(sendMessage);
                }
                return;
            }
            case "Qo`shish➕" -> messageService.addFontMessage(sendMessage);
            case "Fontlar\uD83D\uDD24" -> {
                userPages.put(chatId, 0);
                SendPhoto sendPhoto = new SendPhoto(chatId.toString(), new InputFile());
                sendFontsPageable(sendPhoto);
                if (broke) {
                    sendMessage.setText(fontNames.size() + " ta fontdan " + 5 * (userPages.get(chatId) + 1) + " ta si ko`rsatildi");
                } else {
                    sendMessage.setText(fontNames.size() + " ta fontdan " + fontNames.size() + " ta si ko`rsatildi");
                }
                sendMessage.setReplyMarkup(keyboardService.getReplyKeyboard(2, "Oldingi⬅", "Keyingi➡", "Bosh menyu\uD83C\uDFE0"));
            }
            case "Keyingi➡" -> {
                if ((userPages.get(chatId) + 1) * 5 < fontNames.size()) {
                    userPages.put(chatId, userPages.get(chatId) + 1);
                    sendFontsPageable(new SendPhoto(chatId.toString(), new InputFile()));
                } else {
                    sendMessage.setText("Siz oxirgi sahifaga keldingiz");
                }
            }
            case "Oldingi⬅" -> {
                if (userPages.get(chatId) > 0) {
                    userPages.put(chatId, userPages.get(chatId) - 1);
                    sendFontsPageable(new SendPhoto(chatId.toString(), new InputFile()));
                } else {
                    sendMessage.setText("Siz allaqachon 1-sahifadasiz");
                }
            }
            case "Bosh menyu\uD83C\uDFE0" -> messageService.greetingMessage(sendMessage);
            default -> messageService.greetingMessage(sendMessage);
        }
        sender.execute(sendMessage);
    }

    @SneakyThrows
    public void userPanel(InlineQuery inlineQuery, AnswerInlineQuery answerInlineQuery) {
        if (inlineQuery.getQuery().length() < 5)
            return;
        User user;
        try {
            user = userRepository.findByChatId(inlineQuery.getFrom().getId()).orElseThrow();
        } catch (NoSuchElementException e) {
            answerInlineQuery.setSwitchPmText("Botga o`tish");
            answerInlineQuery.setSwitchPmParameter("12345");
            InputTextMessageContent inputTextMessageContent = new InputTextMessageContent(
                    "Ko`rinishidan siz botga start bosmagan ko`rinasiz. \n" +
                            "Bot bilan ishlash uchun unga start bosishingiz kerak bo`ladi."
            );
            InlineQueryResultArticle inlineQueryResultArticle = new InlineQueryResultArticle(
                    randomUUID().toString(),
                    "Foydalanuvchi topilmadi",
                    inputTextMessageContent,
                    keyboardService.getBotLinkKeyboard(),
                    "https://t.me/sticketjbot",
                    true,
                    "Bot ishlashi uchun unga start bosishingiz kerak bo`ladi. \nYuqoridagi tugma orqali botga o`tib start bosing\uD83D\uDC46",
                    null,
                    0,
                    0
            );
            answerInlineQuery.setResults(Collections.singletonList(inlineQueryResultArticle));
            sender.execute(answerInlineQuery);
            return;
        }
        String[] usersFontNames = getUsersFontNames(user);
        System.out.println(usersFontNames.length);
        List<File> files = imageService.generateImage(inlineQuery.getQuery(), inlineQuery.getId(), usersFontNames);
        List<InlineQueryResult> results = answerInlineQuery.getResults();
        for (File file : files) {
            SendSticker sendSticker = new SendSticker("-1001478286553", new InputFile(file));
            Message executedMessage = sender.execute(sendSticker);
            stickerCount++;
            Sticker sticker = executedMessage.getSticker();
            InlineQueryResultCachedSticker inlineQueryResultCachedSticker = new InlineQueryResultCachedSticker(randomUUID().toString(), sticker.getFileId());
            results.add(inlineQueryResultCachedSticker);
        }
        answerInlineQuery.setResults(results);
        sender.execute(answerInlineQuery);
        queryCount++;
        imageService.deleteFiles(inlineQuery.getId());
    }

    @SneakyThrows
    public void userPanel(Document document, SendMessage sendMessage) {
        Long chatId = Long.valueOf(sendMessage.getChatId());
        User user = userRepository.findByChatId(chatId).orElseThrow();
        if (user.getFonts().size() >= 2) {
            sendMessage.setText("Kechirasiz siz eng ko`pi bilan 2 ta fon qo`sha olasiz");
        } else if (document.getFileName().endsWith(".ttf") || document.getFileName().endsWith(".TTF")) {
            GetFile getFile = new GetFile(document.getFileId());
            org.telegram.telegrambots.meta.api.objects.File executedFile = sender.execute(getFile);
            File fontFile = downloadFile(executedFile.getFilePath(), document.getFileName());
            imageService.initFont(fontFile);
            Font font = new Font(null, fontNames.get(fontNames.size() - 1), user);
            user.getFonts().add(font);
            userRepository.save(user);
            sendMessage.setText("Font qabul qilindi");
            System.out.println(fontNames.get(fontNames.size() - 1));
        }
        sender.execute(sendMessage);
    }

    @SneakyThrows
    public void userPanel(CallbackQuery callbackQuery, SendMessage sendMessage) {
        Long chatId = Long.valueOf(sendMessage.getChatId());
        User user = userRepository.findByChatId(chatId).orElseThrow();
        String callbackData = callbackQuery.getData();
        if (callbackData.startsWith("O`chirish")) {
            callbackData = callbackData.substring(9);
            fontRepository.deleteByFileNameAndUserId(callbackData, user.getId());
            sendMessage.setText("O`chirildi: " + callbackData + " \uD83D\uDDD1");
        } else if (callbackData.startsWith("Qo`shish")) {
            if (user.getFonts().size() < 2) {
                callbackData = callbackData.substring(8);
                Font font = new Font(
                        null,
                        callbackData,
                        user
                );
                user.getFonts().add(font);
                userRepository.save(user);
                sendMessage.setText("Qo`shildi: " + callbackData + " ✅");
            } else {
                sendMessage.setText("Kechirasiz siz eng ko`pi bilan 2 ta fon qo`sha olasiz");
            }
        }
        sender.execute(sendMessage);
    }

    @SneakyThrows
    private File downloadFile(String filePath, String fileName) {
        URL url = new URL(apiUrl + filePath);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = httpURLConnection.getInputStream();
        Path path = Paths.get(fontFolder + "/" + fileName);
        Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        return path.toFile();
    }

    private String[] getUsersFontNames(User user) {
        List<String> userFonts = new ArrayList<>();
        for (Font font : user.getFonts()) {
            userFonts.add(font.getFileName());
        }
        return userFonts.toArray(new String[0]);
    }

    @SneakyThrows
    private void sendFontsPageable(SendPhoto sendPhoto) {
        Long chatId = Long.valueOf(sendPhoto.getChatId());
        User user = userRepository.findByChatId(chatId).orElseThrow();
        int size = 5;
        Integer page = userPages.get(chatId);
        List<File> files = imageService.generateImage("Hello", chatId.toString(), fontNames.toArray(new String[0]));
        for (int i = page * size; i < files.size(); i++) {
            sendPhoto.setPhoto(new InputFile(files.get(i)));
            sendPhoto.setCaption(fontNames.get(i));
            Map<String, String> buttons = new HashMap<>();
            if (user.getFonts().contains(new Font(null, fontNames.get(i),null))){
                buttons.put("Qo`shilgan","Qo`shilgan✔");
            }else {
                buttons.put("Qo`shish" + fontNames.get(i), "Qo`shish➕");
            }
            sendPhoto.setReplyMarkup(keyboardService.getInlineKeyboard(1, buttons));
            sender.execute(sendPhoto);
            if (i + 1 == size) {
                broke = true;
                break;
            }
            Thread.sleep(1000);
        }
    }
}