package kobuki.quadbooki.controller;

import kobuki.quadbooki.domain.User;
import kobuki.quadbooki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String manageMembers(Model model) {
        List<User> members = userService.getAllUsers();
        model.addAttribute("users", members);
        return "/screens/adminUser";
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

}
