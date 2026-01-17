package com.plaintext.core.service;

import com.plaintext.common.model.Post;
import com.plaintext.common.model.User;
import com.plaintext.core.dto.PostRequest;
import com.plaintext.core.dto.PostResponse;
import com.plaintext.core.repository.PostRepository;
import com.plaintext.core.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public PostResponse createPost(String username, PostRequest request) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new com.plaintext.common.exception.ResourceNotFoundException("User not found."));
        checkRateLimit(author);
        Post post = Post.builder()
                .user(author)
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .likesCount(0)
                .commentsCount(0)
                .moderationStatus("PENDING")
                .build();
        Post savedPost = postRepository.save(post);
        return mapToResponse(savedPost);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getExploreFeed() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getUserPosts(String username) {
        return postRepository.findByUserUsernameOrderByCreatedAtDesc(username).stream().map(this::mapToResponse)
                .toList();
    }

    private void checkRateLimit(User author) {
        Post lastPost = postRepository.findTopByUserOrderByCreatedAtDesc(author);
        if (lastPost != null) {
            long minutesSinceLast = ChronoUnit.MINUTES.between(lastPost.getCreatedAt(), LocalDateTime.now());
            if (minutesSinceLast < 5) {
                throw new com.plaintext.common.exception.BadRequestException(
                        "Rate limit exceeded. Please wait 5 minutes between posts.");
            }
        }
    }

    private PostResponse mapToResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .username(post.getUser().getUsername()) // Lazy load triggered here
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
