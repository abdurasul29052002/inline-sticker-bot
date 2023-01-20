package com.example.inlinestickerbot.service;

import com.example.inlinestickerbot.component.Sender;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultPhoto;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.inlinestickerbot.InlineStickerBotApplication.fontFolder;
import static com.example.inlinestickerbot.InlineStickerBotApplication.imageFolder;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MessageService messageService;
    private final Sender sender;
    public static List<String> fontNames;


    @SneakyThrows
    @PostConstruct
    public void init(){
        File[] files = new File(fontFolder).listFiles();
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        List<String> before = new ArrayList<>(Arrays.asList(graphicsEnvironment.getAvailableFontFamilyNames()));
        if (files != null) {
            for (File file : files) {
                graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, file));
            }
        }
        List<String> after = new ArrayList<>(Arrays.asList(graphicsEnvironment.getAvailableFontFamilyNames()));
        after.removeAll(before);
        fontNames = after;
    }

    public void userPanel(String text, SendMessage sendMessage) {
        switch (text) {
            case "/start" -> {
                messageService.greetingMessage(sendMessage);
            }
        }
    }

    @SneakyThrows
    public void userPanel(InlineQuery inlineQuery, AnswerInlineQuery answerInlineQuery) {
        String text = inlineQuery.getQuery();
        List<InlineQueryResult> photos = new ArrayList<>();
        for (int i = 0; i < fontNames.size(); i++) {
            BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);// Represents an image with 8-bit RGBA color components packed into integer pixels.
            Graphics2D graphics2d = image.createGraphics();
            Font font = new Font(fontNames.get(i), Font.BOLD, 75);
            graphics2d.setFont(font);
            FontMetrics fontmetrics = graphics2d.getFontMetrics();
            int width = fontmetrics.stringWidth(text);
            int height = 250;
            graphics2d.dispose();

            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            graphics2d = image.createGraphics();
            graphics2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            graphics2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            graphics2d.setFont(font);
            fontmetrics = graphics2d.getFontMetrics();
            graphics2d.setColor(Color.GREEN);
            graphics2d.drawString(text, 0, fontmetrics.getAscent()+100);
            graphics2d.dispose();
            try {
                ImageIO.write(image, "png", new File(imageFolder+"/"+fontNames.get(i)+"_"+inlineQuery.getId()+".webp"));
                InlineQueryResultPhoto inlineQueryResultPhoto = new InlineQueryResultPhoto(
                        inlineQuery.getId(),
                        "https://home.starfit.uz/sticker/download/"+fontNames.get(i)+"_"+inlineQuery.getId()+".webp"
                );
                photos.add(inlineQueryResultPhoto);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        answerInlineQuery.setResults(photos);
        sender.execute(answerInlineQuery);
    }
}
