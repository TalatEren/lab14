package com.talatdaylan.lab14.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @NotBlank(message = "Author is required")
    @Size(min = 1, max = 255, message = "Author name must be between 1 and 255 characters")
    private String author;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Pattern(regexp = "^(?:[0-9]{10}|[0-9]{13})$", message = "ISBN must be either 10 or 13 digits")
    private String isbn;

    @Min(value = 1800, message = "Published year must be after 1800")
    @Max(value = 2025, message = "Published year cannot be in the future")
    private Integer publishedYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
