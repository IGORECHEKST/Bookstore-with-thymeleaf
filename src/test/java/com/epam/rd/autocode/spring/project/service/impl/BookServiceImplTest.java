package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookDTO bookDTO;

    @BeforeEach
    public void setUp() {
        book = new Book();
        book.setId(1L);
        book.setName("Test Book");

        bookDTO = new BookDTO();
        bookDTO.setName("Test Book");
    }

    @Test
    public void testGetAllBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(Collections.singletonList(book));
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        Page<BookDTO> result = bookService.getAllBooks(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Book", result.getContent().get(0).getName());
        verify(bookRepository).findAll(pageable);
    }

    @Test
    public void testGetBookByName_Success() {
        when(bookRepository.findByName("Test Book")).thenReturn(Optional.of(book));
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        BookDTO result = bookService.getBookByName("Test Book");

        assertNotNull(result);
        assertEquals("Test Book", result.getName());
    }

    @Test
    public void testGetBookByName_NotFound() {
        when(bookRepository.findByName("Nonexistent")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.getBookByName("Nonexistent"));
    }

    @Test
    public void testAddBook_Success() {
        when(bookRepository.findByName("Test Book")).thenReturn(Optional.empty());
        when(modelMapper.map(bookDTO, Book.class)).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        BookDTO result = bookService.addBook(bookDTO);

        assertNotNull(result);
        assertEquals("Test Book", result.getName());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    public void testAddBook_AlreadyExists() {
        when(bookRepository.findByName("Test Book")).thenReturn(Optional.of(book));

        assertThrows(AlreadyExistException.class, () -> bookService.addBook(bookDTO));
    }

    @Test
    public void testUpdateBookByName_Success() {
        when(bookRepository.findByName("Test Book")).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        BookDTO result = bookService.updateBookByName("Test Book", bookDTO);

        assertNotNull(result);
        assertEquals("Test Book", result.getName());
        verify(bookRepository).save(book);
    }

    @Test
    public void testDeleteBookByName_Success() {
        when(bookRepository.findByName("Test Book")).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).delete(book);

        bookService.deleteBookByName("Test Book");

        verify(bookRepository).delete(book);
    }

    @Test
    public void testDeleteBookByName_NotFound() {
        when(bookRepository.findByName("Nonexistent")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.deleteBookByName("Nonexistent"));
    }
}
