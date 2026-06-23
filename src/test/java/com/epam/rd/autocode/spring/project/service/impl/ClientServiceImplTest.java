package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;
    private ClientDTO clientDTO;

    @BeforeEach
    public void setUp() {
        client = new Client();
        client.setId(1L);
        client.setEmail("client@example.com");
        client.setPassword("encodedPassword");

        clientDTO = new ClientDTO();
        clientDTO.setEmail("client@example.com");
        clientDTO.setPassword("password");
        clientDTO.setBalance(BigDecimal.valueOf(100));
    }

    @Test
    public void testGetAllClients() {
        when(clientRepository.findAll()).thenReturn(Collections.singletonList(client));
        when(modelMapper.map(client, ClientDTO.class)).thenReturn(clientDTO);

        List<ClientDTO> result = clientService.getAllClients();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("client@example.com", result.get(0).getEmail());
    }

    @Test
    public void testGetClientByEmail_Success() {
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));
        when(modelMapper.map(client, ClientDTO.class)).thenReturn(clientDTO);

        ClientDTO result = clientService.getClientByEmail("client@example.com");

        assertNotNull(result);
        assertEquals("client@example.com", result.getEmail());
    }

    @Test
    public void testGetClientByEmail_NotFound() {
        when(clientRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> clientService.getClientByEmail("nonexistent@example.com"));
    }

    @Test
    public void testAddClient_Success() {
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.empty());
        when(modelMapper.map(clientDTO, Client.class)).thenReturn(client);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(clientRepository.save(client)).thenReturn(client);
        when(modelMapper.map(client, ClientDTO.class)).thenReturn(clientDTO);

        ClientDTO result = clientService.addClient(clientDTO);

        assertNotNull(result);
        assertEquals("client@example.com", result.getEmail());
        verify(clientRepository).save(client);
    }

    @Test
    public void testAddClient_AlreadyExists() {
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));

        assertThrows(AlreadyExistException.class, () -> clientService.addClient(clientDTO));
    }

    @Test
    public void testUpdateClientByEmail_Success() {
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(clientRepository.save(client)).thenReturn(client);
        when(modelMapper.map(client, ClientDTO.class)).thenReturn(clientDTO);

        ClientDTO result = clientService.updateClientByEmail("client@example.com", clientDTO);

        assertNotNull(result);
        assertEquals("client@example.com", result.getEmail());
        verify(clientRepository).save(client);
    }

    @Test
    public void testDeleteClientByEmail_Success() {
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));
        doNothing().when(clientRepository).delete(client);

        clientService.deleteClientByEmail("client@example.com");

        verify(clientRepository).delete(client);
    }

    @Test
    public void testDeleteClientByEmail_NotFound() {
        when(clientRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> clientService.deleteClientByEmail("nonexistent@example.com"));
    }

    @Test
    public void testUpdateClientByEmail_NotFound() {
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                clientService.updateClientByEmail("client@example.com", clientDTO));
    }

    @Test
    public void testUpdateClientByEmail_PasswordNull() {
        clientDTO.setPassword(null);
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));
        when(clientRepository.save(client)).thenReturn(client);
        when(modelMapper.map(client, ClientDTO.class)).thenReturn(clientDTO);

        ClientDTO result = clientService.updateClientByEmail("client@example.com", clientDTO);

        assertNotNull(result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(clientRepository).save(client);
    }

    @Test
    public void testUpdateClientByEmail_PasswordBlank() {
        clientDTO.setPassword("   ");
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));
        when(clientRepository.save(client)).thenReturn(client);
        when(modelMapper.map(client, ClientDTO.class)).thenReturn(clientDTO);

        ClientDTO result = clientService.updateClientByEmail("client@example.com", clientDTO);

        assertNotNull(result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(clientRepository).save(client);
    }
}
