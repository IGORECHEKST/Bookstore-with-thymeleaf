package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BasketItemDTO;
import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/basket")
@RequiredArgsConstructor
public class BasketController {

    private final BookService bookService;
    private static final String BASKET_KEY = "basket";

    @GetMapping
    public String viewBasket(HttpSession session, Model model) {
        List<BasketItemDTO> basket = getBasket(session);
        model.addAttribute("items", basket);
        return "basket/view";
    }

    @PostMapping("/add")
    public String addToBasket(@RequestParam String bookName, HttpSession session) {
        List<BasketItemDTO> basket = getBasket(session);
        BookDTO book = bookService.getBookByName(bookName);

        basket.stream()
                .filter(item -> item.getBookName().equals(bookName))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + 1),
                        () -> basket.add(new BasketItemDTO(bookName, 1, book.getPrice()))
                );
        return "redirect:/books";
    }

    @PostMapping("/remove")
    public String removeFromBasket(@RequestParam String bookName, HttpSession session) {
        List<BasketItemDTO> basket = getBasket(session);
        basket.removeIf(item -> item.getBookName().equals(bookName));
        return "redirect:/basket";
    }

    private List<BasketItemDTO> getBasket(HttpSession session) {
        List<BasketItemDTO> basket = (List<BasketItemDTO>) session.getAttribute(BASKET_KEY);
        if (basket == null) {
            basket = new ArrayList<>();
            session.setAttribute(BASKET_KEY, basket);
        }
        return basket;
    }
}
