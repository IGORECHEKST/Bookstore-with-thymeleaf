package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeDTO employeeDTO;

    @BeforeEach
    public void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setEmail("emp@example.com");
        employee.setPassword("encodedPassword");

        employeeDTO = new EmployeeDTO();
        employeeDTO.setEmail("emp@example.com");
        employeeDTO.setPassword("password");
    }

    @Test
    public void testGetAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(Collections.singletonList(employee));
        when(modelMapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("emp@example.com", result.get(0).getEmail());
    }

    @Test
    public void testGetEmployeeByEmail_Success() {
        when(employeeRepository.findByEmail("emp@example.com")).thenReturn(Optional.of(employee));
        when(modelMapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.getEmployeeByEmail("emp@example.com");

        assertNotNull(result);
        assertEquals("emp@example.com", result.getEmail());
    }

    @Test
    public void testGetEmployeeByEmail_NotFound() {
        when(employeeRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> employeeService.getEmployeeByEmail("nonexistent@example.com"));
    }

    @Test
    public void testAddEmployee_Success() {
        when(employeeRepository.findByEmail("emp@example.com")).thenReturn(Optional.empty());
        when(modelMapper.map(employeeDTO, Employee.class)).thenReturn(employee);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(modelMapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.addEmployee(employeeDTO);

        assertNotNull(result);
        assertEquals("emp@example.com", result.getEmail());
        verify(employeeRepository).save(employee);
    }

    @Test
    public void testAddEmployee_AlreadyExists() {
        when(employeeRepository.findByEmail("emp@example.com")).thenReturn(Optional.of(employee));

        assertThrows(AlreadyExistException.class, () -> employeeService.addEmployee(employeeDTO));
    }

    @Test
    public void testUpdateEmployeeByEmail_Success() {
        when(employeeRepository.findByEmail("emp@example.com")).thenReturn(Optional.of(employee));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(modelMapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.updateEmployeeByEmail("emp@example.com", employeeDTO);

        assertNotNull(result);
        assertEquals("emp@example.com", result.getEmail());
        verify(employeeRepository).save(employee);
    }

    @Test
    public void testDeleteEmployeeByEmail_Success() {
        when(employeeRepository.findByEmail("emp@example.com")).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        employeeService.deleteEmployeeByEmail("emp@example.com");

        verify(employeeRepository).delete(employee);
    }

    @Test
    public void testDeleteEmployeeByEmail_NotFound() {
        when(employeeRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> employeeService.deleteEmployeeByEmail("nonexistent@example.com"));
    }

    @Test
    public void testUpdateEmployeeByEmail_NotFound() {
        when(employeeRepository.findByEmail("emp@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                employeeService.updateEmployeeByEmail("emp@example.com", employeeDTO));
    }

    @Test
    public void testUpdateEmployeeByEmail_PasswordNull() {
        employeeDTO.setPassword(null);
        when(employeeRepository.findByEmail("emp@example.com")).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(modelMapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.updateEmployeeByEmail("emp@example.com", employeeDTO);

        assertNotNull(result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(employeeRepository).save(employee);
    }

    @Test
    public void testUpdateEmployeeByEmail_PasswordBlank() {
        employeeDTO.setPassword("   ");
        when(employeeRepository.findByEmail("emp@example.com")).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(modelMapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.updateEmployeeByEmail("emp@example.com", employeeDTO);

        assertNotNull(result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(employeeRepository).save(employee);
    }
}
