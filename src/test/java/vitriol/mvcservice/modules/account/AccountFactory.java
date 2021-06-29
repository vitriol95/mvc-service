package vitriol.mvcservice.modules.account;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class AccountFactory {

    @Autowired
    AccountRepository accountRepository;

    public Account newAccount(String nickname) {
        Account account = new Account();
        account.setEmail(nickname + "@naver.com");
        account.setNickname(nickname);
        account.setPassword("123123123");
        accountRepository.save(account);
        return account;
    }
}
