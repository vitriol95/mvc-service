package vitriol.mvcservice.modules.account;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vitriol.mvcservice.modules.account.form.ProfileForm;
import vitriol.mvcservice.modules.account.form.SignUpForm;
import vitriol.mvcservice.modules.account.validator.ProfileFormValidator;
import vitriol.mvcservice.modules.account.validator.SignUpFormValidator;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final SignUpFormValidator signUpFormValidator;
    private final ProfileFormValidator profileFormValidator;
    private final ModelMapper modelMapper;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @InitBinder("profileForm")
    public void initBinder2(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(profileFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute("signUpForm", new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpFormSubmit(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors) {
        if (errors.hasErrors()) {
            return "account/sign-up";
        }
        Account account = accountService.createNewAccount(signUpForm);
        accountService.login(account);
        return "redirect:/";
    }

    @GetMapping("/settings")
    public String updateProfileView(@LoggedInUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, ProfileForm.class));
        return "account/settings";
    }

    @PostMapping("/settings")
    public String updateProfile(@LoggedInUser Account account, @Valid @ModelAttribute ProfileForm profileForm, Errors errors,
                                Model model, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "account/settings";
        }
        accountService.updateProfile(account, profileForm);
        redirectAttributes.addFlashAttribute("message", "수정을 완료하였습니다");
        return "redirect:" + "/settings";
    }
}
