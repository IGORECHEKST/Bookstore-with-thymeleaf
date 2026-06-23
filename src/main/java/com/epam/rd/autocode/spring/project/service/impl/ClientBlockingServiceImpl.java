package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.ClientBlockingStatus;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.ClientBlockingStatusRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.ClientBlockingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientBlockingServiceImpl implements ClientBlockingService {

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ClientBlockingStatusRepository clientBlockingStatusRepository;

    @Override
    @Transactional
    public boolean blockClient(String clientEmail, String reason, String employeeEmail) {
        log.info("Employee {} is attempting to block client {} with reason: {}", employeeEmail, clientEmail, reason);
        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> {
                    log.warn("Client not found for blocking with email: {}", clientEmail);
                    return new NotFoundException("Client not found with email: " + clientEmail);
                });

        Employee employee = employeeRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> {
                    log.warn("Employee not found with email: {}", employeeEmail);
                    return new NotFoundException("Employee not found with email: " + employeeEmail);
                });

        Optional<ClientBlockingStatus> existingStatusOpt = clientBlockingStatusRepository.findByClientEmail(clientEmail);
        ClientBlockingStatus status;

        if (existingStatusOpt.isPresent()) {
            status = existingStatusOpt.get();
            if (status.getIsBlocked()) {
                log.warn("Client {} is already blocked", clientEmail);
                return false;
            }
        } else {
            status = new ClientBlockingStatus();
            status.setClient(client);
        }

        status.setIsBlocked(true);
        status.setBlockedByEmployee(employee);
        status.setBlockReason(reason);
        clientBlockingStatusRepository.save(status);
        log.info("Client {} successfully blocked by Employee {}", clientEmail, employeeEmail);
        return true;
    }

    @Override
    @Transactional
    public boolean unblockClient(String clientEmail, String employeeEmail) {
        log.info("Employee {} is attempting to unblock client {}", employeeEmail, clientEmail);
        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> {
                    log.warn("Client not found for unblocking with email: {}", clientEmail);
                    return new NotFoundException("Client not found with email: " + clientEmail);
                });

        Employee employee = employeeRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> {
                    log.warn("Employee not found with email: {}", employeeEmail);
                    return new NotFoundException("Employee not found with email: " + employeeEmail);
                });

        Optional<ClientBlockingStatus> existingStatusOpt = clientBlockingStatusRepository.findByClientEmail(clientEmail);

        if (existingStatusOpt.isEmpty() || !existingStatusOpt.get().getIsBlocked()) {
            log.warn("Client {} is not blocked, cannot unblock", clientEmail);
            return false;
        }

        ClientBlockingStatus status = existingStatusOpt.get();
        status.setIsBlocked(false);
        status.setBlockedByEmployee(employee);
        status.setBlockReason(null);
        clientBlockingStatusRepository.save(status);
        log.info("Client {} successfully unblocked by Employee {}", clientEmail, employeeEmail);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isClientBlocked(String clientEmail) {
        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new NotFoundException("Client not found with email: " + clientEmail));

        return clientBlockingStatusRepository.findByClientEmail(clientEmail)
                .map(ClientBlockingStatus::getIsBlocked)
                .orElse(false);
    }
}
