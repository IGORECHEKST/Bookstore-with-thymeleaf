package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.dto.OrderWithIdDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.BookItem;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.model.Order;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByClient(String clientEmail) {
        return orderRepository.findAllByClientEmail(clientEmail).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByEmployee(String employeeEmail) {
        return orderRepository.findAllByEmployeeEmail(employeeEmail).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO addOrder(OrderDTO orderDTO) {
        log.info("Attempting to place a new order for client: {}", orderDTO.getClientEmail());
        Client client = clientRepository.findByEmail(orderDTO.getClientEmail())
                .orElseThrow(() -> {
                    log.warn("Client not found for order placement: {}", orderDTO.getClientEmail());
                    return new NotFoundException("Client not found: " + orderDTO.getClientEmail());
                });

        Order order = new Order();
        order.setClient(client);
        order.setOrderDate(LocalDateTime.now());

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (var itemDTO : orderDTO.getBookItems()) {
            Book book = bookRepository.findByName(itemDTO.getBookName())
                    .orElseThrow(() -> {
                        log.warn("Book not found for order item: {}", itemDTO.getBookName());
                        return new NotFoundException("Book not found: " + itemDTO.getBookName());
                    });

            BookItem item = new BookItem();
            item.setOrder(order);
            item.setBook(book);
            item.setQuantity(itemDTO.getQuantity());
            order.getBookItems().add(item);

            totalPrice = totalPrice.add(book.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
        }

        order.setPrice(totalPrice);

        if (client.getBalance().compareTo(totalPrice) < 0) {
            log.warn("Insufficient balance for client {}. Required: {}, Available: {}", client.getEmail(), totalPrice, client.getBalance());
            throw new IllegalArgumentException("Insufficient balance");
        }
        client.setBalance(client.getBalance().subtract(totalPrice));
        clientRepository.save(client);

        Order savedOrder = orderRepository.save(order);
        log.info("Successfully placed order with ID: {} for client: {}. Total price: {}", savedOrder.getId(), client.getEmail(), totalPrice);
        return convertToDTO(savedOrder);
    }

    @Override
    @Transactional
    public OrderDTO assignEmployeeToOrder(Long orderId, String employeeEmail) {
        log.info("Attempting to assign employee {} to order ID: {}", employeeEmail, orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order ID {} not found for employee assignment", orderId);
                    return new NotFoundException("Order not found");
                });
        Employee employee = employeeRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> {
                    log.warn("Employee {} not found for order assignment", employeeEmail);
                    return new NotFoundException("Employee not found");
                });

        order.setEmployee(employee);
        Order updatedOrder = orderRepository.save(order);
        log.info("Successfully assigned employee {} to order ID: {}", employeeEmail, orderId);
        return convertToDTO(updatedOrder);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderWithIdDTO dto = new OrderWithIdDTO();
        dto.setId(order.getId());
        dto.setClientEmail(order.getClient().getEmail());
        if (order.getEmployee() != null) {
            dto.setEmployeeEmail(order.getEmployee().getEmail());
        }
        dto.setOrderDate(order.getOrderDate());
        dto.setPrice(order.getPrice());

        dto.setBookItems(order.getBookItems().stream().map(item -> {
            BookItemDTO bi = new BookItemDTO();
            bi.setBookName(item.getBook().getName());
            bi.setQuantity(item.getQuantity());
            return bi;
        }).collect(Collectors.toList()));

        return dto;
    }
}
