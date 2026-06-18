package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final ClientService clientService;

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("clientDTO", new ClientDTO());
        return "register";
    }

    @PostMapping
    public String registerClient(@Valid @ModelAttribute("clientDTO") ClientDTO clientDTO,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (clientDTO.getPassword() == null || clientDTO.getPassword().isBlank()) {
            bindingResult.rejectValue("password", "error.clientDTO", "Password cannot be empty.");
            return "register";
        }

        try {
            clientService.addClient(clientDTO);
        } catch (Exception e) {
            bindingResult.rejectValue("email", "error.clientDTO", "Email already registered.");
            return "register";
        }

        return "redirect:/login?registered";
    }
}