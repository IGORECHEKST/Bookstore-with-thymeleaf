package com.epam.rd.autocode.spring.project.dto;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    @NotBlank(message = "{validation.book.name.empty}")
    private String name;

    private String genre;

    private AgeGroup ageGroup;

    @NotNull(message = "{validation.book.price.null}")
    @Positive(message = "{validation.book.price.positive}")
    private BigDecimal price;

    private LocalDate publicationDate;

    private String author;

    @Min(value = 1, message = "{validation.book.pages.min}")
    private Integer pages;

    private String characteristics;

    private String description;

    private Language language;
}
