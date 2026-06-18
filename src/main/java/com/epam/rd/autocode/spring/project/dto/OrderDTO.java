package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    @NotBlank(message = "{validation.client.email.empty}")
    private String clientEmail;

    private String employeeEmail;

    private LocalDateTime orderDate;

    @NotNull(message = "{validation.price.null}")
    private BigDecimal price;

    @NotEmpty(message = "{validation.order.items.empty}")
    private List<BookItemDTO> bookItems;
}
