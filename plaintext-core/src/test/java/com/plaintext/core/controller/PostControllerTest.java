package com.plaintext.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plaintext.core.dto.PostRequest;
import com.plaintext.core.dto.PostResponse;
import com.plaintext.core.service.PostService;
import com.plaintext.core.security.AuthTokenFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

    @Test
    void createPost_Success() throws Exception {
        PostRequest request = new PostRequest("Hello World", null);
        java.util.UUID postId = java.util.UUID.randomUUID();
        PostResponse response = new PostResponse(postId, "Hello World", null, "user", 0, 0, LocalDateTime.now(), 0,
                "APPROVED", false);

        when(postService.createPost(eq("user"), any(PostRequest.class))).thenReturn(response);

        // We mock the Principal here manually if using standalone setup,
        // OR we just pass a Principal object if the controller accepts it as an
        // argument.
        // But since the controller takes 'Authentication', we need to mock it.
        // However, MockMvc standalone doesn't easily inject Authentication into
        // arguments without custom HandlerMethodArgumentResolver.
        // A simpler way for a unit test is to trust that Spring Security fills it,
        // and here we just verify logic assuming 'user' is passed.

        // Wait, the controller method signature is:
        // createPost(@RequestBody PostRequest request, Authentication authentication)

        // Standalone MockMvc won't resolve Authentication automatically.
        // So we can mock the behavior by manually setting the Principal in the request
        // builder?
        // Actually, standaloneSetup supports setCustomArgumentResolvers.

        // For simplicity in this unit test, let's assume the controller takes a
        // Principal or we use a Stub.
        // OR we can rely on `SecurityContextHolder`.

        // Let's refactor the Test to be a @WebMvcTest slice test? No, that requires
        // loading context.
        // Let's stick to unit test. We realize verify the argument resolver is complex
        // in pure unit test.
        // Alternative: Pass a mock Principal.

        // Create a mock Authentication object
        Authentication auth = org.mockito.Mockito.mock(Authentication.class);
        when(auth.getName()).thenReturn("user");

        mockMvc.perform(post("/api/posts")
                .principal(auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Hello World"));
    }

    @Test
    void getGlobalFeed_Success() throws Exception {
        java.util.UUID postId = java.util.UUID.randomUUID();
        PostResponse post = new PostResponse(postId, "Hello", null, "user", 0, 0, LocalDateTime.now(), 0, "APPROVED",
                false);
        when(postService.getExploreFeed()).thenReturn(List.of(post));

        mockMvc.perform(get("/api/feed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hello"));
    }
}
