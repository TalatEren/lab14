package com.talatdaylan.lab14.service;

import com.talatdaylan.lab14.model.BlogPost;
import com.talatdaylan.lab14.model.User;
import com.talatdaylan.lab14.repository.BlogPostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogPostServiceTest {

    @Mock
    private BlogPostRepository blogPostRepository;

    @InjectMocks
    private BlogPostService blogPostService;

    private User user1;
    private User user2;
    private BlogPost blogPost1;
    private BlogPost blogPost2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();

        user2 = User.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .build();

        blogPost1 = BlogPost.builder()
                .id(1L)
                .title("Test Post 1")
                .content("Content 1")
                .user(user1)
                .build();

        blogPost2 = BlogPost.builder()
                .id(2L)
                .title("Test Post 2")
                .content("Content 2")
                .user(user2)
                .build();
    }

    @Test
    void getAllPosts_Success() {
        when(blogPostRepository.findAll()).thenReturn(Arrays.asList(blogPost1, blogPost2));

        List<BlogPost> posts = blogPostService.getAllPosts();

        assertNotNull(posts);
        assertEquals(2, posts.size());
        verify(blogPostRepository).findAll();
    }

    @Test
    void getUserPosts_Success() {
        when(blogPostRepository.findByUserId(user1.getId())).thenReturn(List.of(blogPost1));

        List<BlogPost> posts = blogPostService.getUserPosts(user1.getId());

        assertNotNull(posts);
        assertEquals(1, posts.size());
        assertEquals(user1.getId(), posts.get(0).getUser().getId());
        verify(blogPostRepository).findByUserId(user1.getId());
    }

    @Test
    void getPostById_Success() {
        when(blogPostRepository.findById(blogPost1.getId())).thenReturn(Optional.of(blogPost1));

        BlogPost post = blogPostService.getPostById(blogPost1.getId());

        assertNotNull(post);
        assertEquals(blogPost1.getId(), post.getId());
        verify(blogPostRepository).findById(blogPost1.getId());
    }

    @Test
    void getPostById_NotFound() {
        when(blogPostRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            blogPostService.getPostById(999L);
        });
    }

    @Test
    void createPost_Success() {
        when(blogPostRepository.save(any(BlogPost.class))).thenReturn(blogPost1);

        BlogPost newPost = BlogPost.builder()
                .title("New Post")
                .content("New Content")
                .build();

        BlogPost savedPost = blogPostService.createPost(newPost, user1);

        assertNotNull(savedPost);
        assertEquals(user1, savedPost.getUser());
        verify(blogPostRepository).save(any(BlogPost.class));
    }

    @Test
    void updatePost_Success() {
        when(blogPostRepository.findById(blogPost1.getId())).thenReturn(Optional.of(blogPost1));
        when(blogPostRepository.save(any(BlogPost.class))).thenReturn(blogPost1);

        BlogPost updatePost = BlogPost.builder()
                .title("Updated Title")
                .content("Updated Content")
                .build();

        BlogPost updated = blogPostService.updatePost(blogPost1.getId(), updatePost, user1);

        assertNotNull(updated);
        assertEquals("Updated Title", updated.getTitle());
        verify(blogPostRepository).save(any(BlogPost.class));
    }

    @Test
    void updatePost_AccessDenied() {
        when(blogPostRepository.findById(blogPost1.getId())).thenReturn(Optional.of(blogPost1));

        BlogPost updatePost = BlogPost.builder()
                .title("Updated Title")
                .content("Updated Content")
                .build();

        assertThrows(AccessDeniedException.class, () -> {
            blogPostService.updatePost(blogPost1.getId(), updatePost, user2);
        });
    }

    @Test
    void deletePost_Success() {
        when(blogPostRepository.existsByIdAndUserId(blogPost1.getId(), user1.getId())).thenReturn(true);
        doNothing().when(blogPostRepository).deleteById(blogPost1.getId());

        assertDoesNotThrow(() -> blogPostService.deletePost(blogPost1.getId(), user1));
        verify(blogPostRepository).deleteById(blogPost1.getId());
    }

    @Test
    void deletePost_AccessDenied() {
        when(blogPostRepository.existsByIdAndUserId(blogPost1.getId(), user2.getId())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> {
            blogPostService.deletePost(blogPost1.getId(), user2);
        });
        verify(blogPostRepository, never()).deleteById(any());
    }
}
