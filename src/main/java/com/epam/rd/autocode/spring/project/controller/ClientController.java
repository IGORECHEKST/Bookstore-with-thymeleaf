package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.service.ClientBlockingService;
import com.epam.rd.autocode.spring.project.service.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ClientBlockingService clientBlockingService;

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String listClients(Model model) {
        model.addAttribute("clients", clientService.getAllClients());
        return "clients/list";
    }

    @GetMapping("/view/{email}")
    @PreAuthorize("hasRole('EMPLOYEE') or (hasRole('CLIENT') and #email == authentication.name)")
    public String viewProfile(@PathVariable("email") String email, Model model) {
        model.addAttribute("client", clientService.getClientByEmail(email));

        model.addAttribute("isBlocked", clientBlockingService.isClientBlocked(email));

        return "clients/view";
    }

    @GetMapping("/edit/{email}")
    @PreAuthorize("hasRole('EMPLOYEE') or (hasRole('CLIENT') and #email == authentication.name)")
    public String showEditForm(@PathVariable("email") String email, Model model) {
        model.addAttribute("email", email);
        model.addAttribute("clientDTO", clientService.getClientByEmail(email));
        return "clients/edit";
    }

    @PostMapping("/edit/{email}")
    @PreAuthorize("hasRole('EMPLOYEE') or (hasRole('CLIENT') and #email == authentication.name)")
    public String updateProfile(@PathVariable("email") String email,
                                @Valid @ModelAttribute("clientDTO") ClientDTO clientDTO,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("email", email);
            return "clients/edit";
        }
        clientService.updateClientByEmail(email, clientDTO);
        return "redirect:/clients/view/" + email;
    }

    @PostMapping("/block/{email}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String blockClient(@PathVariable("email") String email,
                              @RequestParam(value = "reason", required = false) String reason,
                              Authentication authentication) {
        String employeeEmail = authentication.getName();
        clientBlockingService.blockClient(email, reason, employeeEmail);
        return "redirect:/clients/view/" + email;
    }

    @PostMapping("/unblock/{email}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String unblockClient(@PathVariable("email") String email,
                                Authentication authentication) {
        String employeeEmail = authentication.getName();
        clientBlockingService.unblockClient(email, employeeEmail);
        return "redirect:/clients/view/" + email;
    }

    @PostMapping("/delete/{email}")
    @PreAuthorize("hasRole('EMPLOYEE') or (hasRole('CLIENT') and #email == authentication.name)")
    public String deleteAccount(@PathVariable("email") String email,
                                Authentication authentication,
                                HttpServletRequest request) {

        clientService.deleteClientByEmail(email);

        if (authentication.getName().equals(email)) {
            request.getSession().invalidate();
            return "redirect:/login?deleted";
        }

        return "redirect:/clients";
    }
}
