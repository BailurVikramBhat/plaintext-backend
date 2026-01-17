package com.plaintext.core.service;

import com.plaintext.common.model.Post;
import com.plaintext.common.model.User;
import com.plaintext.core.dto.PostRequest;
import com.plaintext.core.dto.PostResponse;
import com.plaintext.core.repository.PostRepository;
import com.plaintext.core.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void createPost_Success() {
        String username = "testuser";
        PostRequest request = new PostRequest("Hello World", null);
        User user = User.builder().username(username).id(java.util.UUID.randomUUID()).build();
        // Post ID should be UUID. In unit test we can mock it or let it be null if
        // saving returns it.
        // But here we mock return value.
        java.util.UUID postId = java.util.UUID.randomUUID();
        Post post = Post.builder().id(postId).content("Hello World").user(user).build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponse response = postService.createPost(username, request);

        assertNotNull(response);
        assertEquals("Hello World", response.getContent());
        assertEquals(username, response.getUsername());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void getExploreFeed_Success() {
        User user = User.builder().username("user1").build();
        java.util.UUID postId = java.util.UUID.randomUUID();
        Post post = Post.builder().id(postId).content("Content").user(user).build();

        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(post));

        List<PostResponse> feed = postService.getExploreFeed();

        assertEquals(1, feed.size());
        assertEquals("Content", feed.get(0).getContent());
    }
}
