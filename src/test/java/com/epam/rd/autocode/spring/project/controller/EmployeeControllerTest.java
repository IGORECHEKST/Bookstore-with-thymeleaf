package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    public void testListEmployees() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(Collections.singletonList(new EmployeeDTO()));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/list"))
                .andExpect(model().attributeExists("employees"));
    }

    @Test
    public void testShowAddForm() throws Exception {
        mockMvc.perform(get("/employees/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/add"))
                .andExpect(model().attributeExists("employeeDTO"));
    }

    @Test
    public void testAddEmployee_Success() throws Exception {
        mockMvc.perform(post("/employees/add")
                        .param("email", "emp@example.com")
                        .param("name", "John Doe")
                        .param("password", "password123")
                        .param("phone", "+123456789")
                        .param("birthDate", "1990-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"));

        verify(employeeService).addEmployee(any(EmployeeDTO.class));
    }

    @Test
    public void testViewEmployee() throws Exception {
        EmployeeDTO employee = new EmployeeDTO();
        employee.setEmail("emp@example.com");
        when(employeeService.getEmployeeByEmail("emp@example.com")).thenReturn(employee);

        mockMvc.perform(get("/employees/view").param("email", "emp@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/view"))
                .andExpect(model().attributeExists("employee"));
    }

    @Test
    public void testDeleteEmployee() throws Exception {
        doNothing().when(employeeService).deleteEmployeeByEmail("emp@example.com");

        mockMvc.perform(post("/employees/delete").param("email", "emp@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"));

        verify(employeeService).deleteEmployeeByEmail("emp@example.com");
    }

    @Test
    public void testAddEmployee_ValidationError() throws Exception {
        mockMvc.perform(post("/employees/add")
                        .param("email", "invalid-email")
                        .param("name", "")
                        .param("password", "12"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/add"));
    }
}
