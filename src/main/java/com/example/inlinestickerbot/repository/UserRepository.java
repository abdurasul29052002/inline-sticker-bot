package com.example.inlinestickerbot.repository;

import com.example.inlinestickerbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Integer countAllByActive(Boolean active);
    boolean existsByChatId(Long chatId);
    Optional<User> findByChatId(Long chatId);
}
