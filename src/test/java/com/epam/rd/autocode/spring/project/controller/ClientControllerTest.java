package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.service.ClientBlockingService;
import com.epam.rd.autocode.spring.project.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ClientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClientService clientService;

    @Mock
    private ClientBlockingService clientBlockingService;

    @InjectMocks
    private ClientController clientController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clientController).build();
    }

    @Test
    public void testListClients() throws Exception {
        when(clientService.getAllClients()).thenReturn(Collections.singletonList(new ClientDTO()));

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(view().name("clients/list"))
                .andExpect(model().attributeExists("clients"));
    }

    @Test
    public void testViewProfile() throws Exception {
        ClientDTO client = new ClientDTO();
        client.setEmail("client@example.com");
        when(clientService.getClientByEmail("client@example.com")).thenReturn(client);
        when(clientBlockingService.isClientBlocked("client@example.com")).thenReturn(false);

        mockMvc.perform(get("/clients/view/client@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("clients/view"))
                .andExpect(model().attributeExists("client"))
                .andExpect(model().attribute("isBlocked", false));
    }

    @Test
    public void testBlockClient() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("emp@example.com");

        mockMvc.perform(post("/clients/block/client@example.com")
                        .param("reason", "spam")
                        .principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients/view/client@example.com"));

        verify(clientBlockingService).blockClient("client@example.com", "spam", "emp@example.com");
    }

    @Test
    public void testUnblockClient() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("emp@example.com");

        mockMvc.perform(post("/clients/unblock/client@example.com")
                        .principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients/view/client@example.com"));

        verify(clientBlockingService).unblockClient("client@example.com", "emp@example.com");
    }

    @Test
    public void testDeleteAccount_OtherClient() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("emp@example.com");

        mockMvc.perform(post("/clients/delete/client@example.com")
                        .principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients"));

        verify(clientService).deleteClientByEmail("client@example.com");
    }

    @Test
    public void testShowEditForm() throws Exception {
        ClientDTO client = new ClientDTO();
        client.setEmail("client@example.com");
        when(clientService.getClientByEmail("client@example.com")).thenReturn(client);

        mockMvc.perform(get("/clients/edit/client@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("clients/edit"))
                .andExpect(model().attributeExists("clientDTO"))
                .andExpect(model().attribute("email", "client@example.com"));
    }

    @Test
    public void testUpdateProfile_Success() throws Exception {
        mockMvc.perform(post("/clients/edit/client@example.com")
                        .param("email", "client@example.com")
                        .param("name", "John Updated")
                        .param("password", "newpassword123")
                        .param("balance", "50.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients/view/client@example.com"));

        verify(clientService).updateClientByEmail(eq("client@example.com"), any(ClientDTO.class));
    }

    @Test
    public void testUpdateProfile_ValidationError() throws Exception {
        mockMvc.perform(post("/clients/edit/client@example.com")
                        .param("email", "invalid-email")
                        .param("name", "")
                        .param("password", "12")
                        .param("balance", "-5.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("clients/edit"))
                .andExpect(model().attribute("email", "client@example.com"));
    }

    @Test
    public void testDeleteAccount_OwnAccount() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("client@example.com");

        mockMvc.perform(post("/clients/delete/client@example.com")
                        .principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?deleted"));

        verify(clientService).deleteClientByEmail("client@example.com");
    }
}
