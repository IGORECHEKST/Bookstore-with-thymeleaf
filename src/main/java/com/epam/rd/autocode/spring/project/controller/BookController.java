package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    @GetMapping
    public String listBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            Model model) {

        Sort sort;
        if (sortBy.equalsIgnoreCase("name") || sortBy.equalsIgnoreCase("author")) {
            sort = Sort.by(sortDir.equalsIgnoreCase("desc") ?
                    Sort.Order.desc(sortBy).ignoreCase() :
                    Sort.Order.asc(sortBy).ignoreCase());
        } else {
            sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();
        }
        Page<BookDTO> bookPage = bookService.searchBooks(search, PageRequest.of(page, size, sort));

        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("search", search);

        return "books/list";
    }

    @GetMapping("/view/{name}")
    public String viewBook(@PathVariable String name, Model model) {
        model.addAttribute("book", bookService.getBookByName(name));
        return "books/view";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("bookDTO", new BookDTO());
        return "books/add";
    }

    @PostMapping("/add")
    public String addBook(@Valid @ModelAttribute("bookDTO") BookDTO bookDTO,
                          BindingResult bindingResult) {
        log.info("POST /books/add received for book: {}", bookDTO.getName());
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors while adding book: {}", bindingResult.getAllErrors());
            return "books/add";
        }
        bookService.addBook(bookDTO);
        return "redirect:/books";
    }

    @PostMapping("/delete/{name}")
    public String deleteBook(@PathVariable String name) {
        log.info("POST /books/delete/{} received", name);
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
        log.info("POST /books/edit/{} received. New name: {}", originalName, bookDTO.getName());
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors while editing book {}: {}", originalName, bindingResult.getAllErrors());
            model.addAttribute("originalName", originalName);
            return "books/edit";
        }
        bookService.updateBookByName(originalName, bookDTO);
        return "redirect:/books/view/" + bookDTO.getName();
    }
}