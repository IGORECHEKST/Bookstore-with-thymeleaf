package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BookService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<BookDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(book -> modelMapper.map(book, BookDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public BookDTO getBookByName(String name) {
        Book book = bookRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Book not found with name: " + name));
        return modelMapper.map(book, BookDTO.class);
    }

    @Override
    @Transactional
    public BookDTO addBook(BookDTO bookDTO) {
        if (bookRepository.findByName(bookDTO.getName()).isPresent()) {
            throw new AlreadyExistException("Book with name '" + bookDTO.getName() + "' already exists");
        }
        Book book = modelMapper.map(bookDTO, Book.class);
        Book savedBook = bookRepository.save(book);
        return modelMapper.map(savedBook, BookDTO.class);
    }

    @Override
    @Transactional
    public BookDTO updateBookByName(String name, BookDTO bookDTO) {
        Book existingBook = bookRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Book not found with name: " + name));

        existingBook.setGenre(bookDTO.getGenre());
        existingBook.setAgeGroup(bookDTO.getAgeGroup());
        existingBook.setPrice(bookDTO.getPrice());
        existingBook.setPublicationDate(bookDTO.getPublicationDate());
        existingBook.setAuthor(bookDTO.getAuthor());
        existingBook.setPages(bookDTO.getPages());
        existingBook.setCharacteristics(bookDTO.getCharacteristics());
        existingBook.setDescription(bookDTO.getDescription());
        existingBook.setLanguage(bookDTO.getLanguage());

        Book updatedBook = bookRepository.save(existingBook);
        return modelMapper.map(updatedBook, BookDTO.class);
    }

    @Override
    @Transactional
    public void deleteBookByName(String name) {
        Book book = bookRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Book not found with name: " + name));
        bookRepository.delete(book);
    }

}
