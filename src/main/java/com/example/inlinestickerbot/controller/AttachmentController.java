package com.example.inlinestickerbot.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/download")
public class AttachmentController {
    @GetMapping("/{imageName}")
    public void download(@PathVariable Long imageName, HttpServletResponse response){

    }
    @GetMapping("/test")
    public String test(){
        return "Ishlayapti";
    }
}
