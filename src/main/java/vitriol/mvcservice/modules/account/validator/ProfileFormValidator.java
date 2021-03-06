package vitriol.mvcservice.modules.account.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vitriol.mvcservice.modules.account.Account;
import vitriol.mvcservice.modules.account.AccountRepository;
import vitriol.mvcservice.modules.account.UserAccount;
import vitriol.mvcservice.modules.account.form.ProfileForm;

@Component
@RequiredArgsConstructor
public class ProfileFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(ProfileForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProfileForm profileForm = (ProfileForm) target;
        Account byNickname = accountRepository.findByNickname(profileForm.getNickname());

        if (changeNickname(profileForm) && byNickname != null) {
            errors.rejectValue("nickname","wrong.value","해당 닉네임을 이미 사용중입니다.");
        }
    }

    private boolean changeNickname(ProfileForm profileForm) {
        UserAccount principal = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return !profileForm.getNickname().equals(principal.getAccount().getNickname());
    }

}
