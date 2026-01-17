package com.plaintext.core.controller;

import com.plaintext.core.dto.PostRequest;
import com.plaintext.core.dto.PostResponse;
import com.plaintext.core.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PostController {
    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostRequest request,
            Authentication authentication
            ) {
        String username = authentication.getName();
        PostResponse response = postService.createPost(username, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostResponse>> getGlobalFeed() {
        return ResponseEntity.ok(postService.getExploreFeed());
    }

    @GetMapping("/posts/user/{username}")
    public ResponseEntity<List<PostResponse>> getUserPosts(@PathVariable String username) {
        return ResponseEntity.ok(postService.getUserPosts(username));
    }
}
