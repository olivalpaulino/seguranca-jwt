package dobackaofront.seguranca_jwt.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String root() { return "redirect:/login"; }

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/home")
    public String homePage(Model model, Authentication auth) {
        model.addAttribute("user", auth.getName());
        return "home";
    }

}
