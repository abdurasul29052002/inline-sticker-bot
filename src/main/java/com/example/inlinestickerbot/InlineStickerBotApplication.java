package com.example.inlinestickerbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class InlineStickerBotApplication {

    public static final String fontFolder = "src/main/resources/fonts";
    public static final String imageFolder = "C:\\Users\\Abdurasul\\Desktop\\images";
    public static final StringBuilder apiUrl = new StringBuilder("https://api.telegram.org/file/bot");
    public static Integer stickerCount = 0;
    public static Integer queryCount = 0;
    public static Integer userCount = 0;

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
        }, Date.valueOf(LocalDate.of(2023,1,9)),1000*60*60*24);
    }

    @Bean
    public DefaultBotOptions defaultBotOptions(){
        return new DefaultBotOptions();
    }
}
