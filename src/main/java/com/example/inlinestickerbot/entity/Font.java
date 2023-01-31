package com.example.inlinestickerbot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Font {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @ManyToOne(optional = false)
    private User user;

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        } else if (!(obj instanceof Font font)) {
            return false;
        } else {
            return font.getFileName().equals(this.fileName);
        }
    }
}
