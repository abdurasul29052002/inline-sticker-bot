package com.example.inlinestickerbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class InlineStickerBotApplication {

    public static final String fontFolder = "/home/ubuntu/stickerbot/fonts";
    public static final String imageFolder = "/home/ubuntu/stickerbot/images";
    public static final StringBuilder apiUrl = new StringBuilder("https://api.telegram.org/file/bot");
    public static Integer stickerCount = 0;
    public static Integer queryCount = 0;
    public static Integer userCount = 0;
    public static Map<Long, String> admins = new HashMap<>();
    public static Map<Long, Integer> userPages = new HashMap<>();

    public static void main(String[] args) {
        SpringApplication.run(InlineStickerBotApplication.class, args);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                queryCount = 0;
                stickerCount = 0;
                userCount = 0;
                System.out.println("Timer ishladi " + LocalDateTime.now());
            }
        }, Date.valueOf(LocalDate.of(2023,2,3)),1000*60*60*24);

    }

    @Bean
    public DefaultBotOptions defaultBotOptions(){
        return new DefaultBotOptions();
    }
}
