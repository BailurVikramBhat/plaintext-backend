package com.plaintext.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {
    @NotBlank
    @Size(max = 140, message = "Comment must be under 140 chars")
    private String text;
}