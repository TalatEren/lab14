package com.talatdaylan.lab14.controller;

import com.talatdaylan.lab14.model.BlogPost;
import com.talatdaylan.lab14.model.User;
import com.talatdaylan.lab14.repository.UserRepository;
import com.talatdaylan.lab14.service.BlogPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class BlogPostController {

    private final BlogPostService blogPostService;
    private final UserRepository userRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BlogPost>> getAllPosts() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(blogPostService.getAllPosts());
    }

    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BlogPost>> getUserPosts(@PathVariable Long userId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(blogPostService.getUserPosts(userId));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BlogPost> createPost(@Valid @RequestBody BlogPost post) {
        User currentUser = getCurrentUser();
        BlogPost createdPost = blogPostService.createPost(post, currentUser);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createdPost);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BlogPost> updatePost(@PathVariable Long id, @Valid @RequestBody BlogPost post) {
        User currentUser = getCurrentUser();
        BlogPost updatedPost = blogPostService.updatePost(id, post, currentUser);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        blogPostService.deletePost(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
