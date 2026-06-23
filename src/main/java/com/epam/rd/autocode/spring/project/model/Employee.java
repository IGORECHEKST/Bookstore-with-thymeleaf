package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "EMPLOYEES")
@Getter
@Setter
public class Employee extends User {

    private String phone;

    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;

    public Employee() {
    }

    public Employee(Long id, String email, String password, String name, String phone, LocalDate birthDate) {
        super(id, email, password, name);
        this.phone = phone;
        this.birthDate = birthDate;
    }
}