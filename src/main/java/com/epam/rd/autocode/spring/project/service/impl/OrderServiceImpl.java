package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.repo.*;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
        Client client = clientRepository.findByEmail(orderDTO.getClientEmail())
                .orElseThrow(() -> new NotFoundException("Client not found: " + orderDTO.getClientEmail()));

        Order order = new Order();
        order.setClient(client);
        order.setOrderDate(LocalDateTime.now());

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (var itemDTO : orderDTO.getBookItems()) {
            Book book = bookRepository.findByName(itemDTO.getBookName())
                    .orElseThrow(() -> new NotFoundException("Book not found: " + itemDTO.getBookName()));

            BookItem item = new BookItem();
            item.setOrder(order);
            item.setBook(book);
            item.setQuantity(itemDTO.getQuantity());
            order.getBookItems().add(item);

            totalPrice = totalPrice.add(book.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
        }

        order.setPrice(totalPrice);

        if (client.getBalance().compareTo(totalPrice) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        client.setBalance(client.getBalance().subtract(totalPrice));
        clientRepository.save(client);

        return convertToDTO(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDTO assignEmployeeToOrder(Long orderId, String employeeEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        Employee employee = employeeRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        order.setEmployee(employee);
        return convertToDTO(orderRepository.save(order));
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
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
