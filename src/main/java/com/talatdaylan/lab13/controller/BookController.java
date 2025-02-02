package com.talatdaylan.lab14.controller;

import com.talatdaylan.lab14.model.Book;
import com.talatdaylan.lab14.model.User;
import com.talatdaylan.lab14.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book, @AuthenticationPrincipal User user) {
        book.setUser(user);
        return ResponseEntity.ok(bookService.createBook(book));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Book>> getUserBooks(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookService.findByUserId(user.getId()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Book> getBook(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Book book = bookService.findById(id);
        if (!book.getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody Book bookDetails, 
                                         @AuthenticationPrincipal User user) {
        Book book = bookService.findById(id);
        if (!book.getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }
        
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setDescription(bookDetails.getDescription());
        book.setIsbn(bookDetails.getIsbn());
        book.setPublishedYear(bookDetails.getPublishedYear());
        
        return ResponseEntity.ok(bookService.updateBook(book));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Book book = bookService.findById(id);
        if (!book.getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }
        
        bookService.deleteBook(id);
        return ResponseEntity.ok().build();
    }
}
