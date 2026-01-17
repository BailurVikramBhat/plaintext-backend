package com.plaintext.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private UUID id;
    private String content;
    private String imageUrl;
    private String username;
    private Integer likesCount;
    private Integer commentsCount;
    private LocalDateTime createdAt;
    private Integer moderationScore;
    private String moderationStatus;

    @Builder.Default
    private boolean isLiked = false;
}
