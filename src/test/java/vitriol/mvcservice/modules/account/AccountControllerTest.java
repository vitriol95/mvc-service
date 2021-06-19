package vitriol.mvcservice.modules.account;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import vitriol.mvcservice.modules.account.form.SignUpForm;

import javax.transaction.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("vitriol95@naver.com");
        signUpForm.setNickname("vitriol95");
        signUpForm.setPassword("12345678");
        accountService.createNewAccount(signUpForm);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @DisplayName("회원가입 뷰")
    @Test
    void 회원가입_뷰() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 성공")
    @Test
    void 회원가입_성공() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("email", "vitriol951@naver.com")
                .param("nickname", "vitriol951")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("vitriol951@naver.com"));

        assertThat(accountRepository.existsByEmail("vitriol951@naver.com")).isTrue();
        Account vitriol = accountRepository.findByEmail("vitriol951@naver.com");
        assertThat(vitriol).isNotNull();
        assertThat(vitriol.getPassword()).isNotEqualTo("12345678");
        assertThat(vitriol.getCreatedDate()).isBefore(LocalDateTime.now()); // mapped superclass
    }

    @DisplayName("회원가입 실패 - 이미 존재하는 이메일")
    @Test
    void 회원가입_실패_이미_존재하는_이메일() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("email", "vitriol95@naver.com")
                .param("nickname", "vitriol123")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 실패 - 이미 존재하는 닉네임")
    @Test
    void 회원가입_실패_이미_존재하는_닉네임() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("email", "vitriol951@naver.com")
                .param("nickname", "vitriol95")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 실패 - 잘못된 비밀번호")
    @Test
    void 회원가입_실패_잘못된_비밀번호() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("email", "vitriol951@naver.com")
                .param("nickname", "vitriol")
                .param("password", "12")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }
}