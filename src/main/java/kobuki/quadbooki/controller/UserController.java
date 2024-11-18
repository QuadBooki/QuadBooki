package kobuki.quadbooki.controller;

import kobuki.quadbooki.domain.User;
import kobuki.quadbooki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 회원가입 처리
    @PostMapping("/signup")
    public String registerUser(@ModelAttribute User user, @RequestParam String confirmPassword) {
        if (!user.getPassword().equals(confirmPassword)) {
            // 비밀번호와 비밀번호 확인이 일치하지 않으면 오류 처리
            return "error";  // 에러 페이지로 리디렉션
        }
        try {
            userService.register(user);  // 회원가입 서비스 호출
            return "redirect:/screens/login";  // 회원가입 후 로그인 페이지로 리디렉션
        } catch (IllegalStateException e) {
            return "error";  // 이미 등록된 사용자일 경우 예외 처리
        }
    }


    // 로그인 페이지를 GET 요청으로 처리
    @GetMapping("/login")
    public String showLoginPage() {
        return "/screens/login";  // 로그인 페이지를 반환
    }

    // 로그인 폼 데이터 처리 (POST 요청)
    @PostMapping("/login")
    public String login(@RequestParam String userId, @RequestParam String password) {
        String authResult = userService.authenticate(userId, password);
        if (authResult.equals("로그인 성공.")) {
            return "redirect:/";  // 로그인 성공 시 메인 페이지로 리디렉션
        } else {
            return "/screens/loginError";  // 로그인 실패 시 로그인 오류 페이지로 리디렉션
        }
    }

    // 로그아웃 처리
    @PostMapping("/logout")
    public String logout() {
        userService.logout();  // 로그아웃 처리
        return "redirect:/";  // 로그아웃 후 메인 페이지로 리디렉션
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
