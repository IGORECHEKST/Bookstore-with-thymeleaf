package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "client_blocking_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientBlockingStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, unique = true)
    private Client client;

    @Column(nullable = false)
    private Boolean isBlocked = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_by_employee_id")
    private Employee blockedByEmployee;

    @Column(length = 500)
    private String blockReason;
}
