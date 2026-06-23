package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BasketItemDTO;
import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BasketControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BasketController basketController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(basketController).build();
    }

    @Test
    public void testViewBasket() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get("/basket").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("basket/view"))
                .andExpect(model().attributeExists("items"));
    }

    @Test
    public void testAddToBasket_NewItem() throws Exception {
        MockHttpSession session = new MockHttpSession();
        BookDTO book = new BookDTO();
        book.setName("Test Book");
        book.setPrice(BigDecimal.valueOf(50));

        when(bookService.getBookByName("Test Book")).thenReturn(book);

        mockMvc.perform(post("/basket/add")
                        .param("bookName", "Test Book")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        List<BasketItemDTO> basket = (List<BasketItemDTO>) session.getAttribute("basket");
        assertNotNull(basket);
        assertEquals(1, basket.size());
        assertEquals("Test Book", basket.get(0).getBookName());
        assertEquals(1, basket.get(0).getQuantity());
    }

    @Test
    public void testAddToBasket_ExistingItem() throws Exception {
        MockHttpSession session = new MockHttpSession();
        List<BasketItemDTO> basket = new ArrayList<>();
        basket.add(new BasketItemDTO("Test Book", 1, BigDecimal.valueOf(50)));
        session.setAttribute("basket", basket);

        BookDTO book = new BookDTO();
        book.setName("Test Book");
        book.setPrice(BigDecimal.valueOf(50));

        when(bookService.getBookByName("Test Book")).thenReturn(book);

        mockMvc.perform(post("/basket/add")
                        .param("bookName", "Test Book")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        List<BasketItemDTO> updatedBasket = (List<BasketItemDTO>) session.getAttribute("basket");
        assertNotNull(updatedBasket);
        assertEquals(1, updatedBasket.size());
        assertEquals(2, updatedBasket.get(0).getQuantity()); // 1 + 1 = 2
    }

    @Test
    public void testRemoveFromBasket() throws Exception {
        MockHttpSession session = new MockHttpSession();
        List<BasketItemDTO> basket = new ArrayList<>();
        basket.add(new BasketItemDTO("Test Book", 1, BigDecimal.valueOf(50)));
        session.setAttribute("basket", basket);

        mockMvc.perform(post("/basket/remove")
                        .param("bookName", "Test Book")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/basket"));

        List<BasketItemDTO> updatedBasket = (List<BasketItemDTO>) session.getAttribute("basket");
        assertNotNull(updatedBasket);
        assertTrue(updatedBasket.isEmpty());
    }
}
