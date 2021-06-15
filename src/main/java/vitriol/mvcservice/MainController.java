package vitriol.mvcservice;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vitriol.mvcservice.modules.account.Account;
import vitriol.mvcservice.modules.account.LoggedInUser;

@Controller
public class MainController {

    @GetMapping("/")
    public String main(@LoggedInUser Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }


}
