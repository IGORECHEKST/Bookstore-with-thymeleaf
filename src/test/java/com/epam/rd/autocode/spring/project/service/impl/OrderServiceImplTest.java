package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.model.Order;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Client client;
    private Employee employee;
    private Book book;
    private Order order;
    private OrderDTO orderDTO;

    @BeforeEach
    public void setUp() {
        client = new Client();
        client.setId(1L);
        client.setEmail("client@example.com");
        client.setBalance(BigDecimal.valueOf(200));

        employee = new Employee();
        employee.setId(2L);
        employee.setEmail("emp@example.com");

        book = new Book();
        book.setId(3L);
        book.setName("Test Book");
        book.setPrice(BigDecimal.valueOf(50));

        order = new Order();
        order.setId(10L);
        order.setClient(client);
        order.setEmployee(employee);
        order.setOrderDate(LocalDateTime.now());
        order.setPrice(BigDecimal.valueOf(50));
        order.setBookItems(new ArrayList<>());

        orderDTO = new OrderDTO();
        orderDTO.setClientEmail("client@example.com");
        BookItemDTO bookItemDTO = new BookItemDTO();
        bookItemDTO.setBookName("Test Book");
        bookItemDTO.setQuantity(1);
        orderDTO.setBookItems(Collections.singletonList(bookItemDTO));
    }

    @Test
    public void testGetOrdersByClient() {
        when(orderRepository.findAllByClientEmail("client@example.com")).thenReturn(Collections.singletonList(order));

        List<OrderDTO> result = orderService.getOrdersByClient("client@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("client@example.com", result.get(0).getClientEmail());
    }

    @Test
    public void testGetOrdersByEmployee() {
        when(orderRepository.findAllByEmployeeEmail("emp@example.com")).thenReturn(Collections.singletonList(order));

        List<OrderDTO> result = orderService.getOrdersByEmployee("emp@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("emp@example.com", result.get(0).getEmployeeEmail());
    }

    @Test
    public void testAddOrder_Success() {
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));
        when(bookRepository.findByName("Test Book")).thenReturn(Optional.of(book));
        when(clientRepository.save(client)).thenReturn(client);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDTO result = orderService.addOrder(orderDTO);

        assertNotNull(result);
        verify(clientRepository).save(client);
        verify(orderRepository).save(any(Order.class));
        assertEquals(BigDecimal.valueOf(150), client.getBalance()); // 200 - 50 = 150
    }

    @Test
    public void testAddOrder_ClientNotFound() {
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.addOrder(orderDTO));
    }

    @Test
    public void testAddOrder_BookNotFound() {
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));
        when(bookRepository.findByName("Test Book")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.addOrder(orderDTO));
    }

    @Test
    public void testAddOrder_InsufficientBalance() {
        client.setBalance(BigDecimal.valueOf(10)); // price is 50
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));
        when(bookRepository.findByName("Test Book")).thenReturn(Optional.of(book));

        assertThrows(IllegalArgumentException.class, () -> orderService.addOrder(orderDTO));
    }

    @Test
    public void testAssignEmployeeToOrder_Success() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(employeeRepository.findByEmail("emp@example.com")).thenReturn(Optional.of(employee));
        when(orderRepository.save(order)).thenReturn(order);

        OrderDTO result = orderService.assignEmployeeToOrder(10L, "emp@example.com");

        assertNotNull(result);
        verify(orderRepository).save(order);
    }

    @Test
    public void testAssignEmployeeToOrder_OrderNotFound() {
        when(orderRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.assignEmployeeToOrder(10L, "emp@example.com"));
    }

    @Test
    public void testAssignEmployeeToOrder_EmployeeNotFound() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(employeeRepository.findByEmail("emp@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.assignEmployeeToOrder(10L, "emp@example.com"));
    }
}
