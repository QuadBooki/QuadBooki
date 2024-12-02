package kobuki.quadbooki.controller;

import kobuki.quadbooki.domain.Book;
import kobuki.quadbooki.domain.Rent;
import kobuki.quadbooki.domain.User;
import kobuki.quadbooki.service.BookService;
import kobuki.quadbooki.service.RentService;
import kobuki.quadbooki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private BookService bookService;
    @Autowired
    private RentService rentService;

    @GetMapping("/users")
    public String listUsers(
            @RequestParam(value = "userName", defaultValue = "") String userName,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        Page<User> users = userService.findUsers(userName, page, size);
        // 페이지 수 검증
        int totalPages = users.getTotalPages();
        if (totalPages <= 0) {
            totalPages = 1; // 기본값 설정
        }
        model.addAttribute("users", users.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("userName", userName); // 검색어 유지
        return "screens/adminUser";
    }

    @PostMapping("/users/set-rentable/{id}")
    public String setRentable(@PathVariable Long id) {
        userService.updateUserRentable(id, true); // user_rentable = true
        return "redirect:/admin/users"; // 상태 변경 후 다시 목록으로 리다이렉트
    }

    @PostMapping("/users/set-unrentable/{id}")
    public String setUnrentable(@PathVariable Long id) {
        userService.updateUserRentable(id, false); // user_rentable = false
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(
            @PathVariable Long id,
            @RequestParam("userRentCount") int userRentCount,
            RedirectAttributes redirectAttributes) {
        if (userRentCount > 0) {
            redirectAttributes.addFlashAttribute("message", "대여중인 책이 있는 사용자입니다.");
        } else {
            userService.removeUserByAdmin(id);
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/search")
    public String searchUsers(@RequestParam("userName") String userName, Model model) {
        List<User> users = userService.findUsersByName(userName);
        model.addAttribute("users", users);
        return "screens/adminUser"; // 검색 결과를 같은 페이지에 표시
    }
//--------------------------------------------------여기부턴 도서 관리----------------------------------------------//
    @GetMapping("/books")
    public String listBooks(
        @RequestParam(value = "title", defaultValue = "") String title,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        Model model) {
        Page<Book> books = bookService.findBooks(title, page, size);
        model.addAttribute("books", books.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", books.getTotalPages());
        model.addAttribute("title", title); // 검색어 유지
        return "screens/adminBook";
    }

    @GetMapping("/books/search")
    public String searchBooks(@RequestParam("title") String title, Model model) {
        List<Book> books = bookService.findBooksByTitle(title);
        model.addAttribute("books", books);
        return "screens/adminBook"; // 검색 결과를 같은 페이지에 표시
    }

    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return "redirect:/admin/books";
    }

    @GetMapping("/books/add")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book()); // 빈 책 객체 전달
        return "screens/addBook"; // 책 추가 페이지
    }

    @PostMapping("/books/add")
    public String addBook(@ModelAttribute Book book) {
        bookService.saveBook(book);
        return "redirect:/admin/books";
    }

    //--------------------------------------------------여기부턴 대여 요청 관리----------------------------------------------//
    @GetMapping("/rents")
    public String listRents(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        Page<Rent> rents = rentService.getAllRents(page, size);
        model.addAttribute("rents", rents.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rents.getTotalPages());
        return "screens/adminRent";
    }

    @PostMapping("/rents/approve/{id}")
    public String approveRent(@PathVariable Long id) {
        rentService.approveRent(id);
        return "redirect:/admin/rents";
    }

    @PostMapping("/rents/reject/{id}")
    public String rejectRent(@PathVariable Long id) {
        rentService.rejectRent(id);
        return "redirect:/admin/rents";
    }

    //--------------------------------------------------여기부턴 승인된 대여 관리 ----------------------------------------------//

    @GetMapping("/activeRents")
    public String getApprovedRents(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        // 승인된 대여 목록 가져오기
        Page<Rent> rents = rentService.getApprovedRents(page, size);
        model.addAttribute("rents", rents.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rents.getTotalPages());
        return "screens/adminActiveRent";
    }

    @PostMapping("/activeRents/return/{id}")
    public String returnBook(@PathVariable Long id) {
        rentService.returnRent(id); // 반납 처리
        return "redirect:/admin/activeRents";
    }

}
