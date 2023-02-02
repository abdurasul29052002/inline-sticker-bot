package com.example.inlinestickerbot.service;

import com.example.inlinestickerbot.config.BotConfig;
import com.example.inlinestickerbot.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.inlinestickerbot.InlineStickerBotApplication.*;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final BotConfig botConfig;
    private final UserRepository userRepository;
    public static List<String> fontNames;
    public static GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();

    @PostConstruct
    public void init() {
        apiUrl.append(botConfig.getToken()).append("/");
        initFont(new File(fontFolder + "/kindness-love-script.ttf"));
        admins.put(968877318L, "ADMIN");
        admins.put(1324394249L, "ADMIN");
    }

    @SneakyThrows
    public void initFont(File file) {
        List<String> before = new ArrayList<>(Arrays.asList(graphicsEnvironment.getAvailableFontFamilyNames()));
        graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, file));
        List<String> after = new ArrayList<>(Arrays.asList(graphicsEnvironment.getAvailableFontFamilyNames()));
        after.removeAll(before);
        fontNames = after;
        System.out.println(fontNames.size());
    }

    @SneakyThrows
    public List<File> generateImage(String text, @NonNull String queryId, String... fonts) {
        List<File> files = new ArrayList<>();
        for (int i = 0; i < fonts.length; i++) {
            String imageName = fonts[i] + "_" + queryId + ".png";
            BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);// Represents an image with 8-bit RGBA color components packed into integer pixels.
            Graphics2D graphics2d = image.createGraphics();
            Font font = new Font(fonts[i], Font.BOLD, 75);
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
            graphics2d.setColor(Color.PINK);
            graphics2d.drawString(text, 0, fontmetrics.getAscent() + 100);
            graphics2d.dispose();
            Scalr.resize(image, Scalr.Mode.AUTOMATIC, 512);
            ImageIO.write(image, "png", new File(imageFolder + "/" + imageName));
            files.add(new File(imageFolder + "/" + imageName));
        }
        return files;
    }

    public void deleteFiles(String queryId) {
        for (String fontName : fontNames) {
            File file = new File(imageFolder + "/" + fontName + "_" + queryId + ".png");
            file.delete();
        }
    }
}
