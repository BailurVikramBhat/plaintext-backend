package com.plaintext.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    @NotBlank(message = "Content cannot be empty.")
    @Size(max = 280, message = "Post cannot exceed 280 characters.")
    private String content;

    private String imageUrl;
}
