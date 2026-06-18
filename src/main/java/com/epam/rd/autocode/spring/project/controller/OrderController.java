package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BasketItemDTO;
import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/client/{email}")
    @PreAuthorize("hasRole('EMPLOYEE') or (hasRole('CLIENT') and #email == authentication.name)")
    public String getClientOrders(@PathVariable String email, Model model) {
        model.addAttribute("orders", orderService.getOrdersByClient(email));
        model.addAttribute("email", email);
        return "orders/client-list";
    }

    @GetMapping("/employee/{email}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String getEmployeeOrders(@PathVariable String email, Model model) {
        model.addAttribute("orders", orderService.getOrdersByEmployee(email));
        model.addAttribute("email", email);
        return "orders/employee-list";
    }

    @GetMapping("/checkout")
    @PreAuthorize("hasRole('CLIENT')")
    public String checkout(HttpSession session, Authentication authentication) {
        List<BasketItemDTO> basket = (List<BasketItemDTO>) session.getAttribute("basket");
        if (basket == null || basket.isEmpty()) {
            return "redirect:/basket";
        }

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setClientEmail(authentication.getName());
        orderDTO.setBookItems(basket.stream().map(bi -> {
            BookItemDTO item = new BookItemDTO();
            item.setBookName(bi.getBookName());
            item.setQuantity(bi.getQuantity());
            return item;
        }).collect(Collectors.toList()));

        orderService.addOrder(orderDTO);
        session.removeAttribute("basket");
        return "redirect:/orders/client/" + authentication.getName();
    }

    @PostMapping("/confirm/{orderId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String confirmOrder(@PathVariable Long orderId, Authentication authentication) {
        orderService.assignEmployeeToOrder(orderId, authentication.getName());
        return "redirect:/orders/employee/" + authentication.getName();
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('CLIENT', 'EMPLOYEE')")
    public String showOrderForm(Model model) {
        model.addAttribute("orderDTO", new OrderDTO());
        return "orders/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('CLIENT', 'EMPLOYEE')")
    public String placeOrder(@Valid @ModelAttribute("orderDTO") OrderDTO orderDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "orders/create";
        orderService.addOrder(orderDTO);
        return "redirect:/orders/client/" + orderDTO.getClientEmail();
    }
}