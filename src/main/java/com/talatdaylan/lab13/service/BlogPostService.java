package com.talatdaylan.lab14.service;

import com.talatdaylan.lab14.model.BlogPost;
import com.talatdaylan.lab14.model.User;
import com.talatdaylan.lab14.repository.BlogPostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogPostService {
    private final BlogPostRepository blogPostRepository;

    public List<BlogPost> getAllPosts() {
        return blogPostRepository.findAll();
    }

    public List<BlogPost> getUserPosts(Long userId) {
        return blogPostRepository.findByUserId(userId);
    }

    public BlogPost getPostById(Long id) {
        return blogPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog post not found with id: " + id));
    }

    @Transactional
    public BlogPost createPost(BlogPost post, User user) {
        post.setUser(user);
        return blogPostRepository.save(post);
    }

    @Transactional
    public BlogPost updatePost(Long id, BlogPost updatedPost, User user) {
        BlogPost existingPost = getPostById(id);
        
        if (!existingPost.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have permission to update this post");
        }

        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());

        return blogPostRepository.save(existingPost);
    }

    @Transactional
    public void deletePost(Long id, User user) {
        if (!blogPostRepository.existsByIdAndUserId(id, user.getId())) {
            throw new AccessDeniedException("You don't have permission to delete this post");
        }
        blogPostRepository.deleteById(id);
    }
}
