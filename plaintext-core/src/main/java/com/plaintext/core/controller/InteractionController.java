package com.plaintext.core.controller;

import com.plaintext.core.dto.CommentRequest;
import com.plaintext.core.dto.CommentResponse;
import com.plaintext.core.service.InteractionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class InteractionController {

    private final InteractionService interactionService;

    // --- LIKES ---

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> toggleLike(@PathVariable UUID postId, Authentication auth) {
        interactionService.toggleLike(auth.getName(), postId);
        return ResponseEntity.ok("Like toggled");
    }

    // --- COMMENTS ---

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable UUID postId,
            @Valid @RequestBody CommentRequest request,
            Authentication auth) {
        return ResponseEntity.ok(interactionService.addComment(auth.getName(), postId, request));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable UUID postId) {
        return ResponseEntity.ok(interactionService.getComments(postId));
    }

    // --- FOLLOWS ---

    @PostMapping("/users/{username}/follow")
    public ResponseEntity<?> followUser(@PathVariable String username, Authentication auth) {
        interactionService.followUser(auth.getName(), username);
        return ResponseEntity.ok("Followed " + username);
    }

    @DeleteMapping("/users/{username}/follow")
    public ResponseEntity<?> unfollowUser(@PathVariable String username, Authentication auth) {
        interactionService.unfollowUser(auth.getName(), username);
        return ResponseEntity.ok("Unfollowed " + username);
    }
}