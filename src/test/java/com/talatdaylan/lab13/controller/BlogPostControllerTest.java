package com.talatdaylan.lab14.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talatdaylan.lab14.config.SecurityTestConfig;
import com.talatdaylan.lab14.model.BlogPost;
import com.talatdaylan.lab14.model.User;
import com.talatdaylan.lab14.repository.UserRepository;
import com.talatdaylan.lab14.security.JwtAuthenticationFilter;
import com.talatdaylan.lab14.security.JwtService;
import com.talatdaylan.lab14.service.BlogPostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BlogPostController.class)
@Import(SecurityTestConfig.class)
class BlogPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BlogPostService blogPostService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private BlogPost testPost;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        testPost = BlogPost.builder()
                .id(1L)
                .title("Test Post")
                .content("Test Content")
                .user(testUser)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any())).thenReturn("test.jwt.token");
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAllPosts_Success() throws Exception {
        when(blogPostService.getAllPosts()).thenReturn(Arrays.asList(testPost));

        mockMvc.perform(get("/api/posts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Test Post"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getUserPosts_Success() throws Exception {
        when(blogPostService.getUserPosts(testUser.getId())).thenReturn(Arrays.asList(testPost));

        mockMvc.perform(get("/api/posts/user/{userId}", testUser.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Test Post"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createPost_Success() throws Exception {
        when(blogPostService.createPost(any(BlogPost.class), eq(testUser))).thenReturn(testPost);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPost)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Post"));

        verify(blogPostService).createPost(any(BlogPost.class), eq(testUser));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updatePost_Success() throws Exception {
        when(blogPostService.updatePost(eq(1L), any(BlogPost.class), eq(testUser))).thenReturn(testPost);

        mockMvc.perform(put("/api/posts/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPost)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Post"));

        verify(blogPostService).updatePost(eq(1L), any(BlogPost.class), eq(testUser));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deletePost_Success() throws Exception {
        doNothing().when(blogPostService).deletePost(eq(1L), eq(testUser));

        mockMvc.perform(delete("/api/posts/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(blogPostService).deletePost(eq(1L), eq(testUser));
    }
}
