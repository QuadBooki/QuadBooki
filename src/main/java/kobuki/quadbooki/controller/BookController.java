package kobuki.quadbooki.controller;

import kobuki.quadbooki.domain.Book;
import kobuki.quadbooki.dto.BookListDto;
import kobuki.quadbooki.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
@RequiredArgsConstructor // final 붙은 변수 자동 생성자
@RequestMapping("/book")
public class BookController {
    private final BookService bookService;

    @GetMapping("/library")
    public String library(Model model) {
        List<BookListDto> books = bookService.getBooksList();
        model.addAttribute("books", books);
        return "screens/library";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Book book = bookService.getBook(id);
        model.addAttribute("book", book);
        return "screens/book-detail";
    }



}