package com.epam.rd.autocode.spring.project.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderWithIdDTO extends OrderDTO {
    private Long id;

    public OrderWithIdDTO() {
        super();
    }

    public OrderWithIdDTO(String clientEmail, String employeeEmail, LocalDateTime orderDate, BigDecimal price, List<BookItemDTO> bookItems, Long id) {
        super(clientEmail, employeeEmail, orderDate, price, bookItems);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
