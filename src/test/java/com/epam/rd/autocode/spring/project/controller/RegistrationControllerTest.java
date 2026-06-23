package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.web.servlet.view.InternalResourceViewResolver;

@ExtendWith(MockitoExtension.class)
public class RegistrationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private RegistrationController registrationController;

    @BeforeEach
    public void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(registrationController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    public void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("clientDTO"));
    }

    @Test
    public void testRegisterClient_Success() throws Exception {
        when(clientService.addClient(any(ClientDTO.class))).thenReturn(new ClientDTO());

        mockMvc.perform(post("/register")
                        .param("email", "client@example.com")
                        .param("name", "John Client")
                        .param("password", "password123")
                        .param("balance", "100.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        verify(clientService).addClient(any(ClientDTO.class));
    }

    @Test
    public void testRegisterClient_ValidationError() throws Exception {
        mockMvc.perform(post("/register")
                        .param("email", "invalid-email")
                        .param("name", "")
                        .param("password", "12")
                        .param("balance", "-10.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    public void testRegisterClient_ServiceThrowsException() throws Exception {
        when(clientService.addClient(any(ClientDTO.class))).thenThrow(new RuntimeException("Email already exists"));

        mockMvc.perform(post("/register")
                        .param("email", "client@example.com")
                        .param("name", "John Client")
                        .param("password", "password123")
                        .param("balance", "100.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("clientDTO", "email"));
    }

    @Test
    public void testRegisterClient_PasswordNullDirect() {
        ClientDTO dto = new ClientDTO("client@example.com", null, "John Client", BigDecimal.TEN);
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "clientDTO");

        String view = registrationController.registerClient(dto, bindingResult);

        assertEquals("register", view);
        assertTrue(bindingResult.hasErrors());
        assertEquals("Password cannot be empty.", bindingResult.getFieldError("password").getDefaultMessage());
    }

    @Test
    public void testRegisterClient_PasswordBlankDirect() {
        ClientDTO dto = new ClientDTO("client@example.com", "   ", "John Client", BigDecimal.TEN);
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "clientDTO");

        String view = registrationController.registerClient(dto, bindingResult);

        assertEquals("register", view);
        assertTrue(bindingResult.hasErrors());
        assertEquals("Password cannot be empty.", bindingResult.getFieldError("password").getDefaultMessage());
    }
}
