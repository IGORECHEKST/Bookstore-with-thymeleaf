package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(client -> modelMapper.map(client, ClientDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDTO getClientByEmail(String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found with email: " + email));
        return modelMapper.map(client, ClientDTO.class);
    }

    @Override
    @Transactional
    public ClientDTO addClient(ClientDTO clientDTO) {
        log.info("Attempting to add client with email: {}", clientDTO.getEmail());
        if (clientRepository.findByEmail(clientDTO.getEmail()).isPresent()) {
            log.warn("Client with email {} already exists", clientDTO.getEmail());
            throw new AlreadyExistException("Client with email " + clientDTO.getEmail() + " already exists");
        }
        Client client = modelMapper.map(clientDTO, Client.class);
        client.setPassword(passwordEncoder.encode(clientDTO.getPassword()));
        Client savedClient = clientRepository.save(client);
        log.info("Successfully added client with ID: {}", savedClient.getId());
        return modelMapper.map(savedClient, ClientDTO.class);
    }

    @Override
    @Transactional
    public ClientDTO updateClientByEmail(String email, ClientDTO clientDTO) {
        log.info("Attempting to update client with email: {}", email);
        Client existingClient = clientRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Client not found for update with email: {}", email);
                    return new NotFoundException("Client not found with email: " + email);
                });

        existingClient.setName(clientDTO.getName());
        existingClient.setBalance(clientDTO.getBalance());
        if (clientDTO.getPassword() != null && !clientDTO.getPassword().isBlank()) {
            existingClient.setPassword(passwordEncoder.encode(clientDTO.getPassword()));
        }

        Client updatedClient = clientRepository.save(existingClient);
        log.info("Successfully updated client with email: {}", email);
        return modelMapper.map(updatedClient, ClientDTO.class);
    }

    @Override
    @Transactional
    public void deleteClientByEmail(String email) {
        log.info("Attempting to delete client with email: {}", email);
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Client not found for deletion with email: {}", email);
                    return new NotFoundException("Client not found with email: " + email);
                });
        clientRepository.delete(client);
        log.info("Successfully deleted client with email: {}", email);
    }
}
