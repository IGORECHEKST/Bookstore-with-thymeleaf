package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    @NotBlank(message = "{validation.email.empty}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.password.empty}")
    @Size(min = 6, message = "{validation.password.length}")
    private String password;

    @NotBlank(message = "{validation.name.empty}")
    private String name;

    @NotNull(message = "{validation.balance.null}")
    @PositiveOrZero(message = "{validation.balance.negative}")
    private BigDecimal balance;
}
