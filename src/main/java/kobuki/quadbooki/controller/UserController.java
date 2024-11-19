package kobuki.quadbooki.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kobuki.quadbooki.domain.User;
import kobuki.quadbooki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 회원가입 처리
    @PostMapping("/signup")
    public String registerUser(@ModelAttribute User user,
                               @RequestParam String confirmPassword,
                               @RequestParam(required = false) Boolean isDuplicateChecked) {
        // 중복 확인 여부 체크
        if (isDuplicateChecked == null || !isDuplicateChecked) {
            return "redirect:/screens/signup?error=duplicateCheck"; // 중복 확인 실패
        }

        // 비밀번호와 비밀번호 확인 일치 여부 체크
        if (!user.getPassword().equals(confirmPassword)) {
            return "redirect:/screens/signup?error=passwordMismatch"; // 비밀번호 불일치
        }

        try {
            // 회원가입 처리
            userService.register(user);
            return "redirect:/screens/login?registered=true"; // 회원가입 성공
        } catch (IllegalStateException e) {
            return "redirect:/screens/signup?error=userExists"; // 사용자 중복
        }
    }


    @GetMapping("/check-duplicate")
    public ResponseEntity<Map<String, Boolean>> checkDuplicate(@RequestParam String userId) {
        boolean isDuplicate = userService.isUserIdDuplicate(userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isDuplicate", isDuplicate);
        return ResponseEntity.ok(response);
    }

    // 로그인 페이지를 GET 요청으로 처리
    @GetMapping("/login")
    public String showLoginPage(HttpSession session, Model model) {
        Object isLoggedIn = session.getAttribute("isLoggedIn");
        model.addAttribute("isLoggedIn", isLoggedIn != null && (Boolean) isLoggedIn);
        return "screens/login";
    }

    // 로그인 폼 데이터 처리 (POST 요청)
    @PostMapping("/login")
    public String login(@RequestParam String userId,
                        @RequestParam String password,
                        HttpServletRequest request,
                        Model model) {
        Optional<User> authenticatedUser = userService.authenticate(userId, password);

        if (authenticatedUser.isPresent()) {
            HttpSession session = request.getSession();
            session.setAttribute("user", authenticatedUser.get());
            session.setAttribute("isLoggedIn", true); // 로그인 상태 설정
            return "redirect:/"; // 홈 페이지로 리디렉션
        } else {
            model.addAttribute("message", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "/screens/login";
        }
    }
    @GetMapping("/logout")
    public String gLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/"; // 로그아웃 후 홈으로 리다이렉트
    }

    // 로그아웃 처리
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 세션 무효화
        }
        return "redirect:/"; // 메인 페이지로 리다이렉트
    }
    // 전화번호 변경
    @PutMapping("/update-phone")
    public String updatePhoneNumber(@RequestParam String userId, @RequestParam String newPhoneNumber) {
        return userService.changePhoneNumber(userId, newPhoneNumber);
    }

    // 회원 탈퇴
    @DeleteMapping("/delete")
    public String deleteUser(@RequestParam String userId) {
        return userService.deleteAccount(userId);
    }

    // 모든 회원 조회
    @GetMapping("/")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // 대여 권수 제한을 초과하지 않은 회원 조회
    @GetMapping("/rent-limit")
    public List<User> getUsersWithRentLimit(@RequestParam int maxRentCount) {
        return userService.findUsersByRentLimit(maxRentCount);
    }

    // 관리자가 회원을 삭제하는 메서드
    @DeleteMapping("/admin/{id}")
    public String deleteUserByAdmin(@PathVariable Long id) {
        return userService.removeUserByAdmin(id);
    }
}
