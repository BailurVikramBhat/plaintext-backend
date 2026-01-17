package com.plaintext.core.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CommentResponse {
    private UUID id;
    private String text;
    private String username; // Author
    private LocalDateTime createdAt;
}