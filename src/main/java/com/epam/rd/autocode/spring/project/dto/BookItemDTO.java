package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BookItemDTO {

    @NotBlank(message = "{validation.book.name.empty}")
    private String bookName;

    @NotNull(message = "{validation.quantity.null}")
    @Min(value = 1, message = "{validation.quantity.min}")
    private Integer quantity;

    public BookItemDTO() {
    }

    public BookItemDTO(String bookName, Integer quantity) {
        this.bookName = bookName;
        this.quantity = quantity;
    }
}