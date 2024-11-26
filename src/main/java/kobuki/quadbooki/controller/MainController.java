package kobuki.quadbooki.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String index() {
        return "screens/main";
    }

    @GetMapping("/screens/main")
    public String screens() {
        return "screens/main";
    }

    @GetMapping("/screens/login")
    public String showLoginPage() {
        return "screens/login"; // templates/screens/login.html로 연결
    }

    @GetMapping("/screens/signup")
    public String showSignupPage() {
        return "/screens/signup"; // templates/screens/signup.html로 연결
    }

    @GetMapping("/screens/usage")
    public String screensUsage() {
        return "screens/usage";
    }

//    @GetMapping("/screens/library")
//    public String screensLibrary() {
//        return "screens/library";
//    }

    @GetMapping("/screens/register")
    public String screensRegister() {
        return "screens/register";
    }

    @GetMapping("/screens/mypage")
    public String screensMyPage() {
        return "screens/mypage";
    }

    @GetMapping("/screens/book-detail")
    public String screensBookDetail() {
        return "screens/book-detail";
    }
}
