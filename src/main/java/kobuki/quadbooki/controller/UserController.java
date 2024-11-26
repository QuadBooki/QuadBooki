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

import java.util.*;

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

            User user = authenticatedUser.get();
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("isLoggedIn", true); // 로그인 상태 설정

            if(user.isAdmin()){
                return "/screens/adminUser";
            }

            return "redirect:/"; // 홈 페이지로 리디렉션
        }
        else {
            model.addAttribute("message", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "screens/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 기존 세션이 있으면 가져오기
        if (session != null) {
            session.invalidate(); // 세션을 무효화
        }
        return "redirect:/"; // 로그아웃 후 메인 페이지로 리디렉션
    }

    // 마이페이지
    @GetMapping("/mypage")
    public String showMyPage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null) {
            return "redirect:/users/login?error=notLoggedIn";
        }
        model.addAttribute("user", loggedInUser);
        model.addAttribute("userId", loggedInUser.getUserId());
        return "screens/mypage";
    }

    //전화번호 수정
    @PostMapping("/update-phone")
    public String updatePhoneNumber(
            @RequestParam String newPhoneNumber,
            HttpSession session,
            Model model) {
        User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null) {
            return "redirect:/users/login?error=notLoggedIn";
        }

        String userId = loggedInUser.getUserId(); // 세션에서 userId를 바로 가져옵니다.

        try {
            // 전화번호 변경 서비스 호출
            String resultMessage = userService.changePhoneNumber(userId, newPhoneNumber);

            // 세션 무효화
            session.invalidate();

            // 로그인 페이지로 이동하며 메시지 전달
            model.addAttribute("message", "전화번호가 성공적으로 변경되었습니다. 다시 로그인해주세요.");
            return "screens/login"; // 로그인 페이지 템플릿으로 이동
        } catch (IllegalStateException e) {
            // 실패 메시지 추가
            model.addAttribute("message", e.getMessage());
            return "screens/mypage";
        }
    }

    // 회원 탈퇴
    @PostMapping("/delete")
    public String deleteAccount(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");

        String result = userService.deleteAccount(userId);

        if ("회원 탈퇴가 완료되었습니다.".equals(result)) {
            session.invalidate();
            return "redirect:/";
        } else {
            model.addAttribute("message", result);
            return "error";
        }
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
