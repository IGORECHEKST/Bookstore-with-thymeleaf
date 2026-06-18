package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public String listBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            Model model) {

        Page<BookDTO> bookPage = bookService.getAllBooks(PageRequest.of(page, size, Sort.by(sortBy)));

        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);

        return "books/list";
    }

    public String viewBook(@PathVariable String name, Model model) {
        model.addAttribute("book", bookService.getBookByName(name));
        return "books/view";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("bookDTO", new BookDTO());
        return "books/add";
    }

    public String addBook(@Valid @ModelAttribute("bookDTO") BookDTO bookDTO,
                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "books/add";
        }
        bookService.addBook(bookDTO);
        return "redirect:/books";
    }

    @PostMapping("/delete/{name}")
    public String deleteBook(@PathVariable String name) {
        bookService.deleteBookByName(name);
        return "redirect:/books";
    }

    @GetMapping("/edit/{name}")
    public String showEditForm(@PathVariable String name, Model model) {
        model.addAttribute("bookDTO", bookService.getBookByName(name));
        model.addAttribute("originalName", name);
        return "books/edit";
    }

    @PostMapping("/edit/{originalName}")
    public String updateBook(@PathVariable String originalName,
                             @Valid @ModelAttribute("bookDTO") BookDTO bookDTO,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("originalName", originalName);
            return "books/edit";
        }
        bookService.updateBookByName(originalName, bookDTO);
        return "redirect:/books/view/" + bookDTO.getName();
    }
}