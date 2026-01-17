package com.plaintext.core.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PostResponse {
    private UUID id;
    private String content;
    private String imageUrl;
    private String username;
    private Integer likesCount;
    private Integer commentsCount;
    private LocalDateTime createdAt;

    @Builder.Default
    private boolean isLiked = false;
}
