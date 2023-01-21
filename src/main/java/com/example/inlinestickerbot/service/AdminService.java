package com.example.inlinestickerbot.service;

import com.example.inlinestickerbot.component.Sender;
import com.example.inlinestickerbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import static com.example.inlinestickerbot.InlineStickerBotApplication.*;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final KeyboardService keyboardService;
    private final UserRepository userRepository;
    private final Sender sender;

    @SneakyThrows
    public void adminPanel(String text, SendMessage sendMessage){
        switch (text) {
            case "/start" -> {
                sendMessage.setText("Assalomu alaykum.\nAdmin panelga hush kelibsiz");
                ReplyKeyboardMarkup replyKeyboard = keyboardService.getReplyKeyboard(
                        2,
                        "Foydalanuvchilarga habar jo`natish",
                        "Foydalanuvchilar soni",
                        "Statistika"
                );
                sendMessage.setReplyMarkup(replyKeyboard);
            }
            case "Foydalanuvchilarga habar jo`natish" -> {
                sendMessage.setText("Bu bo`lim hali ishlovda");
            }
            case "Foydalanuvchilar soni" ->{
             sendMessage.setText("Foydalanuvchilar soni: " + userRepository.count() + "\nActive: " + userRepository.countAllByActive(true) + "\nBlock: " + userRepository.countAllByActive(false));
            }
            case "Statistika" ->{
                sendMessage.setText("Bugungi kun bo`yicha: \nQuerylar soni: " + queryCount + "\nStickerlar soni: " + stickerCount + "\nYangi obunachilar: " + userCount);
            }
        }
        sender.execute(sendMessage);
    }
}
