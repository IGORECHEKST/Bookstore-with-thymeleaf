package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    public void testListBooks() throws Exception {
        when(bookService.searchBooks(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(new BookDTO())));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/list"))
                .andExpect(model().attributeExists("books"));
    }

    @Test
    public void testViewBook() throws Exception {
        BookDTO book = new BookDTO();
        book.setName("MyBook");
        when(bookService.getBookByName("MyBook")).thenReturn(book);

        mockMvc.perform(get("/books/view/MyBook"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/view"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    public void testShowAddForm() throws Exception {
        mockMvc.perform(get("/books/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/add"))
                .andExpect(model().attributeExists("bookDTO"));
    }

    @Test
    public void testAddBook_Success() throws Exception {
        mockMvc.perform(post("/books/add")
                        .param("name", "NewBook")
                        .param("genre", "FICTION")
                        .param("price", "29.99")
                        .param("pages", "300")
                        .param("publicationDate", "2023-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService).addBook(any(BookDTO.class));
    }

    @Test
    public void testDeleteBook() throws Exception {
        doNothing().when(bookService).deleteBookByName("MyBook");

        mockMvc.perform(post("/books/delete/MyBook"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService).deleteBookByName("MyBook");
    }

    @Test
    public void testListBooks_SortByNameDesc() throws Exception {
        when(bookService.searchBooks(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(new BookDTO())));

        mockMvc.perform(get("/books")
                        .param("sortBy", "name")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/list"));
    }

    @Test
    public void testListBooks_SortByAuthorAsc() throws Exception {
        when(bookService.searchBooks(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(new BookDTO())));

        mockMvc.perform(get("/books")
                        .param("sortBy", "author")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/list"));
    }

    @Test
    public void testListBooks_SortByOtherFieldDesc() throws Exception {
        when(bookService.searchBooks(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(new BookDTO())));

        mockMvc.perform(get("/books")
                        .param("sortBy", "price")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/list"));
    }

    @Test
    public void testListBooks_SortByOtherFieldAsc() throws Exception {
        when(bookService.searchBooks(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(new BookDTO())));

        mockMvc.perform(get("/books")
                        .param("sortBy", "price")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/list"));
    }

    @Test
    public void testAddBook_ValidationError() throws Exception {
        mockMvc.perform(post("/books/add")
                        .param("name", "") // validation error (blank)
                        .param("price", "-10")) // validation error (negative)
                .andExpect(status().isOk())
                .andExpect(view().name("books/add"));
    }

    @Test
    public void testShowEditForm() throws Exception {
        BookDTO book = new BookDTO();
        book.setName("MyBook");
        when(bookService.getBookByName("MyBook")).thenReturn(book);

        mockMvc.perform(get("/books/edit/MyBook"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/edit"))
                .andExpect(model().attributeExists("bookDTO"))
                .andExpect(model().attribute("originalName", "MyBook"));
    }

    @Test
    public void testUpdateBook_Success() throws Exception {
        mockMvc.perform(post("/books/edit/OriginalBookName")
                        .param("name", "UpdatedBook")
                        .param("price", "29.99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/view/UpdatedBook"));

        verify(bookService).updateBookByName(eq("OriginalBookName"), any(BookDTO.class));
    }

    @Test
    public void testUpdateBook_ValidationError() throws Exception {
        mockMvc.perform(post("/books/edit/OriginalBookName")
                        .param("name", "") // validation error
                        .param("price", "0")) // validation error (must be positive)
                .andExpect(status().isOk())
                .andExpect(view().name("books/edit"))
                .andExpect(model().attribute("originalName", "OriginalBookName"));
    }
}
