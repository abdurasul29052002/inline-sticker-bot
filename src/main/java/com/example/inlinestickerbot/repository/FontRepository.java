package com.example.inlinestickerbot.repository;

import com.example.inlinestickerbot.entity.Font;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FontRepository extends JpaRepository<Font, Long> {
    @Transactional
    void deleteByFileNameAndUserId(String fileName, Long user_id);
}
