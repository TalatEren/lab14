package com.talatdaylan.lab14.service;

import com.talatdaylan.lab14.model.Book;
import com.talatdaylan.lab14.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    @Transactional
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Transactional(readOnly = true)
    public List<Book> findByUserId(Long userId) {
        return bookRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
    }

    @Transactional
    public Book updateBook(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
