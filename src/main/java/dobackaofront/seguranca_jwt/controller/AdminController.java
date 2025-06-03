package dobackaofront.seguranca_jwt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String adminPage(Model model, Authentication auth) {
        model.addAttribute("user", auth.getName());
        return "admin"; // renderiza o template admin.html
    }
}

