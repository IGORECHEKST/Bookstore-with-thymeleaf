package com.epam.rd.autocode.spring.project.service;

public interface ClientBlockingService {

    boolean blockClient(String clientEmail, String reason, String employeeEmail);

    boolean unblockClient(String clientEmail, String employeeEmail);

    boolean isClientBlocked(String clientEmail);
}