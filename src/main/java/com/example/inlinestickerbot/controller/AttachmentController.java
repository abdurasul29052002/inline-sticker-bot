package com.example.inlinestickerbot.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;

import static com.example.inlinestickerbot.InlineStickerBotApplication.imageFolder;

@RestController
@RequestMapping("/download")
public class AttachmentController {
    @SneakyThrows
    @GetMapping("/{imageName}")
    public void download(@PathVariable Long imageName, HttpServletResponse response){
        File file = new File(imageFolder+"/"+imageName);
        response.setContentType("image/webp");
        response.addHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
        FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
    }
    @GetMapping("/test")
    public String test(){
        return "Ishlayapti";
    }
}
