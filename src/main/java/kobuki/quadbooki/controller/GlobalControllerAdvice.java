package kobuki.quadbooki.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("isLoggedIn")
    public Boolean addLoginStatusToModel(HttpSession session) {
        if (session == null) {
            return false;
        }
        Object isLoggedIn = session.getAttribute("isLoggedIn");
        return isLoggedIn != null && (Boolean) isLoggedIn;
    }
}