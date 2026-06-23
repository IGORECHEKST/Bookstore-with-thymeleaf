package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BasketItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    public void testGetClientOrders() throws Exception {
        when(orderService.getOrdersByClient("client@example.com")).thenReturn(Collections.singletonList(new OrderDTO()));

        mockMvc.perform(get("/orders/client/client@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/client-list"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    public void testGetEmployeeOrders() throws Exception {
        when(orderService.getOrdersByEmployee("emp@example.com")).thenReturn(Collections.singletonList(new OrderDTO()));

        mockMvc.perform(get("/orders/employee/emp@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/employee-list"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    public void testCheckout_EmptyBasket() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get("/orders/checkout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/basket"));
    }

    @Test
    public void testCheckout_Success() throws Exception {
        MockHttpSession session = new MockHttpSession();
        List<BasketItemDTO> basket = new ArrayList<>();
        basket.add(new BasketItemDTO("Test Book", 1, BigDecimal.valueOf(50)));
        session.setAttribute("basket", basket);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("client@example.com");

        mockMvc.perform(get("/orders/checkout")
                        .session(session)
                        .principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/client/client@example.com"));

        verify(orderService).addOrder(any(OrderDTO.class));
    }

    @Test
    public void testConfirmOrder() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("emp@example.com");

        mockMvc.perform(post("/orders/confirm/123")
                        .principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/employee/emp@example.com"));

        verify(orderService).assignEmployeeToOrder(123L, "emp@example.com");
    }

    @Test
    public void testShowOrderForm() throws Exception {
        mockMvc.perform(get("/orders/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/create"))
                .andExpect(model().attributeExists("orderDTO"));
    }

    @Test
    public void testCheckout_NullBasket() throws Exception {
        // MockHttpSession is empty, session attribute "basket" is null.
        mockMvc.perform(get("/orders/checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/basket"));
    }

    @Test
    public void testPlaceOrder_Success() throws Exception {
        when(orderService.addOrder(any(OrderDTO.class))).thenReturn(new OrderDTO());

        mockMvc.perform(post("/orders/create")
                        .param("clientEmail", "client@example.com")
                        .param("price", "100.00")
                        .param("bookItems[0].bookName", "Test Book")
                        .param("bookItems[0].quantity", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/client/client@example.com"));

        verify(orderService).addOrder(any(OrderDTO.class));
    }

    @Test
    public void testPlaceOrder_ValidationError() throws Exception {
        // Let's assume OrderDTO validation triggers when clientEmail is blank
        // Let's verify if clientEmail is annotated with NotBlank. We can inspect OrderDTO if needed, but passing empty string is standard.
        mockMvc.perform(post("/orders/create")
                        .param("clientEmail", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/create"));
    }
}
