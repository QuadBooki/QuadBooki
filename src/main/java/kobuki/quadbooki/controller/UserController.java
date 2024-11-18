package kobuki.quadbooki.controller;

import kobuki.quadbooki.domain.User;
import kobuki.quadbooki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 회원가입 처리
    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        userService.register(user);
        return "회원가입이 완료되었습니다.";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String userId, @RequestParam String password) {
        return userService.authenticate(userId, password);
    }

    // 로그아웃 처리
    @PostMapping("/logout")
    public String logout() {
        return userService.logout();
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
