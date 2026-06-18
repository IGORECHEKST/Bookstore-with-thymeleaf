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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientBlockingServiceImpl implements ClientBlockingService {

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ClientBlockingStatusRepository clientBlockingStatusRepository;

    @Override
    @Transactional
    public boolean blockClient(String clientEmail, String reason, String employeeEmail) {
        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new NotFoundException("Client not found with email: " + clientEmail));

        Employee employee = employeeRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new NotFoundException("Employee not found with email: " + employeeEmail));

        Optional<ClientBlockingStatus> existingStatusOpt = clientBlockingStatusRepository.findByClientEmail(clientEmail);
        ClientBlockingStatus status;

        if (existingStatusOpt.isPresent()) {
            status = existingStatusOpt.get();
            if (status.getIsBlocked()) {
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
        return true;
    }

    @Override
    @Transactional
    public boolean unblockClient(String clientEmail, String employeeEmail) {
        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new NotFoundException("Client not found with email: " + clientEmail));

        Employee employee = employeeRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new NotFoundException("Employee not found with email: " + employeeEmail));

        Optional<ClientBlockingStatus> existingStatusOpt = clientBlockingStatusRepository.findByClientEmail(clientEmail);

        if (existingStatusOpt.isEmpty() || !existingStatusOpt.get().getIsBlocked()) {
            return false;
        }

        ClientBlockingStatus status = existingStatusOpt.get();
        status.setIsBlocked(false);
        status.setBlockedByEmployee(employee);
        status.setBlockReason(null);
        clientBlockingStatusRepository.save(status);
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
