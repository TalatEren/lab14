package com.talatdaylan.lab14.repository;

import com.talatdaylan.lab14.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findByUserId(Long userId);
    boolean existsByIdAndUserId(Long id, Long userId);
}
