package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "CLIENTS")
@Getter
@Setter
public class Client extends User {

    @Column(nullable = false)
    private BigDecimal balance;

    public Client() {
    }

    public Client(Long id, String email, String password, String name, BigDecimal balance) {
        super(id, email, password, name);
        this.balance = balance;
    }
}