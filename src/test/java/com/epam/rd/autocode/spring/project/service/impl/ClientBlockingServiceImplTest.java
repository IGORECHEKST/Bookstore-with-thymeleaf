package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.ClientBlockingStatus;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.ClientBlockingStatusRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientBlockingServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ClientBlockingStatusRepository clientBlockingStatusRepository;

    @InjectMocks
    private ClientBlockingServiceImpl clientBlockingService;

    private Client client;
    private Employee employee;
    private ClientBlockingStatus status;

    @BeforeEach
    public void setUp() {
        client = new Client();
        client.setId(1L);
        client.setEmail("client@example.com");

        employee = new Employee();
        employee.setId(2L);
        employee.setEmail("emp@example.com");

        status = new ClientBlockingStatus();
        status.setId(10L);
        status.setClient(client);
        status.setIsBlocked(false);
    }

    @Test
    public void testBlockClient_Success() {
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));
        when(employeeRepository.findByEmail("emp@example.com")).thenReturn(Optional.of(employee));
        when(clientBlockingStatusRepository.findByClientEmail("client@example.com")).thenReturn(Optional.empty());

        boolean result = clientBlockingService.blockClient("client@example.com", "spam", "emp@example.com");

        assertTrue(result);
        verify(clientBlockingStatusRepository).save(any(ClientBlockingStatus.class));
    }

    @Test
    public void testBlockClient_AlreadyBlocked() {
        status.setIsBlocked(true);
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));
        when(employeeRepository.findByEmail("emp@example.com")).thenReturn(Optional.of(employee));
        when(clientBlockingStatusRepository.findByClientEmail("client@example.com")).thenReturn(Optional.of(status));

        boolean result = clientBlockingService.blockClient("client@example.com", "spam", "emp@example.com");

        assertFalse(result);
        verify(clientBlockingStatusRepository, never()).save(any(ClientBlockingStatus.class));
    }

    @Test
    public void testUnblockClient_Success() {
        status.setIsBlocked(true);
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));
        when(employeeRepository.findByEmail("emp@example.com")).thenReturn(Optional.of(employee));
        when(clientBlockingStatusRepository.findByClientEmail("client@example.com")).thenReturn(Optional.of(status));

        boolean result = clientBlockingService.unblockClient("client@example.com", "emp@example.com");

        assertTrue(result);
        assertFalse(status.getIsBlocked());
        verify(clientBlockingStatusRepository).save(status);
    }

    @Test
    public void testUnblockClient_NotBlocked() {
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));
        when(employeeRepository.findByEmail("emp@example.com")).thenReturn(Optional.of(employee));
        when(clientBlockingStatusRepository.findByClientEmail("client@example.com")).thenReturn(Optional.of(status)); // status has isBlocked = false

        boolean result = clientBlockingService.unblockClient("client@example.com", "emp@example.com");

        assertFalse(result);
        verify(clientBlockingStatusRepository, never()).save(any(ClientBlockingStatus.class));
    }

    @Test
    public void testIsClientBlocked() {
        status.setIsBlocked(true);
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));
        when(clientBlockingStatusRepository.findByClientEmail("client@example.com")).thenReturn(Optional.of(status));

        boolean result = clientBlockingService.isClientBlocked("client@example.com");

        assertTrue(result);
    }
}
