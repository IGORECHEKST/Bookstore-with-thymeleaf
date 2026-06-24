package com.epam.rd.autocode.spring.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import com.epam.rd.autocode.spring.project.service.BookService;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class BookStoreServiceSolutionApplicationTests {

    @Autowired
    private BookService bookService;

    @Test
    void contextLoads() {
        assertNotNull(bookService);
        bookService.searchBooks("", PageRequest.of(0, 10));
    }

}
