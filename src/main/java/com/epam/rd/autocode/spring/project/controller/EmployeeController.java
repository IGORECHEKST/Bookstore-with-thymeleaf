package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "employees/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("employeeDTO", new EmployeeDTO());
        return "employees/add";
    }

    @PostMapping("/add")
    public String addEmployee(@Valid @ModelAttribute("employeeDTO") EmployeeDTO employeeDTO,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "employees/add";
        }
        employeeService.addEmployee(employeeDTO);
        return "redirect:/employees";
    }

    @GetMapping("/view")
    public String viewEmployee(@RequestParam("email") String email, Model model) {
        EmployeeDTO employee = employeeService.getEmployeeByEmail(email);
        model.addAttribute("employee", employee);
        return "employees/view";
    }

    @PostMapping("/delete")
    public String deleteEmployee(@RequestParam("email") String email) {
        employeeService.deleteEmployeeByEmail(email);
        return "redirect:/employees";
    }
}
