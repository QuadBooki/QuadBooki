package kobuki.quadbooki.controller;

import jakarta.servlet.http.HttpSession;
import kobuki.quadbooki.domain.User;
import kobuki.quadbooki.service.RentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Slf4j
@Controller
public class RentController {

    @Autowired
    private  RentService rentService;

    @PostMapping("/rents")
    public String createRent(@RequestParam Long bookId, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/screens/login";
        }

        try {
            rentService.createRent(loggedInUser.getId(), bookId, LocalDate.now(), LocalDate.now().plusWeeks(2));
            redirectAttributes.addFlashAttribute("successMessage", "대여가 완료되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/book/detail/" + bookId; // 책 상세 페이지로 돌아감
    }


}
