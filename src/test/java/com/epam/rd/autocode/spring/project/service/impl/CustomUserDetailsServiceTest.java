package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.ClientBlockingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ClientBlockingService clientBlockingService;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void testLoadUserByUsername_EmployeeFound() {
        Employee emp = new Employee();
        emp.setEmail("emp@example.com");
        emp.setPassword("encodedPassword");
        when(employeeRepository.findByEmail("emp@example.com")).thenReturn(Optional.of(emp));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("emp@example.com");

        assertNotNull(userDetails);
        assertEquals("emp@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE")));
        verify(clientRepository, never()).findByEmail(anyString());
    }

    @Test
    public void testLoadUserByUsername_ClientFound_NotBlocked() {
        Client client = new Client();
        client.setEmail("client@example.com");
        client.setPassword("clientPassword");
        when(employeeRepository.findByEmail("client@example.com")).thenReturn(Optional.empty());
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));
        when(clientBlockingService.isClientBlocked("client@example.com")).thenReturn(false);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("client@example.com");

        assertNotNull(userDetails);
        assertEquals("client@example.com", userDetails.getUsername());
        assertEquals("clientPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT")));
    }

    @Test
    public void testLoadUserByUsername_ClientFound_Blocked() {
        Client client = new Client();
        client.setEmail("client@example.com");
        client.setPassword("clientPassword");
        when(employeeRepository.findByEmail("client@example.com")).thenReturn(Optional.empty());
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));
        when(clientBlockingService.isClientBlocked("client@example.com")).thenReturn(true);

        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("client@example.com"));
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        when(employeeRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        when(clientRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("nonexistent@example.com"));
    }
}
