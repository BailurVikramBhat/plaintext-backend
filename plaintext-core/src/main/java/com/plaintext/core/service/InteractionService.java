package com.plaintext.core.service;

import com.plaintext.common.model.*;
import com.plaintext.core.dto.CommentRequest;
import com.plaintext.core.dto.CommentResponse;
import com.plaintext.core.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InteractionService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;

    @Transactional
    public void toggleLike(String username, UUID postId) {
        User user = getUser(username);
        Post post = getPost(postId);
        if(postLikeRepository.existsByUserIdAndPostId(user.getId(), post.getId())) {
            // unlike
            postLikeRepository.deleteByUserIdAndPostId(user.getId(), post.getId());
            post.setLikesCount(post.getLikesCount() - 1);
        } else {
            // like
            PostLike like = PostLike.builder().user(user).post(post).build();
            postLikeRepository.save(like);
            post.setLikesCount(post.getLikesCount() + 1);
        }
        postRepository.save(post);
    }

    @Transactional
    public CommentResponse addComment(String username, UUID postId, CommentRequest request) {
        User user = getUser(username);
        Post post = getPost(postId);

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .content(request.getText())
                .build();

        Comment saved = commentRepository.saveAndFlush(comment);

        // Update post counter
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        LocalDateTime responseTime = saved.getCreatedAt() != null ? saved.getCreatedAt() : LocalDateTime.now();


        return CommentResponse.builder()
                .id(saved.getId())
                .text(saved.getContent())
                .username(user.getUsername())
                .createdAt(responseTime)
                .build();
    }

    public List<CommentResponse> getComments(UUID postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(c -> CommentResponse.builder()
                        .id(c.getId())
                        .text(c.getContent())
                        .username(c.getUser().getUsername())
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void followUser(String followerUsername, String followingUsername) {
        if (followerUsername.equals(followingUsername)) {
            throw new RuntimeException("You cannot follow yourself.");
        }

        User follower = getUser(followerUsername);
        User following = getUser(followingUsername);

        if (!followRepository.existsByFollowerIdAndFollowingId(follower.getId(), following.getId())) {
            Follow follow = Follow.builder().follower(follower).following(following).build();
            followRepository.save(follow);
        }
    }

    @Transactional
    public void unfollowUser(String followerUsername, String followingUsername) {
        User follower = getUser(followerUsername);
        User following = getUser(followingUsername);

        followRepository.deleteByFollowerIdAndFollowingId(follower.getId(), following.getId());
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Post getPost(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }
}
