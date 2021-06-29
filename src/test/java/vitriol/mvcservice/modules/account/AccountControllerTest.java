package vitriol.mvcservice.modules.account;

import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.hamcrest.collection.IsIterableWithSize;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import vitriol.mvcservice.modules.account.form.SignUpForm;
import vitriol.mvcservice.modules.post.PostFactory;
import vitriol.mvcservice.modules.post.PostService;
import vitriol.mvcservice.modules.post.ReplyFactory;

import javax.transaction.Transactional;

import java.util.Set;

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
    @Autowired
    private AccountFactory accountFactory;
    @Autowired
    private PostFactory postFactory;
    @Autowired
    private ReplyFactory replyFactory;
    @Autowired
    private PostService postService;

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("vitriol95@naver.com");
        signUpForm.setNickname("vitriol95");
        signUpForm.setPassword("12345678");
        accountService.createNewAccount(signUpForm);

        SignUpForm signUpForm2 = new SignUpForm();
        signUpForm2.setEmail("test@naver.com");
        signUpForm2.setNickname("test");
        signUpForm2.setPassword("12345678");
        accountService.createNewAccount(signUpForm2);

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

    // ------ 설정 관련 ------ //
    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("회원 정보 변경 뷰")
    @Test
    void 회원_정보_변경_뷰() throws Exception {

        mockMvc.perform(get("/settings"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/settings"))
                .andExpect(model().attributeExists("profileForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(authenticated());
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("회원 정보 변경 - 입력값 정상")
    @Test
    void 회원_정보_변경_입력값_정상() throws Exception {

        mockMvc.perform(post("/settings")
                .param("nickname", "vitriol951")
                .param("bio", "bio")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings"))
                .andExpect(view().name("redirect:/settings"))
                .andExpect(flash().attributeExists("message")); // 오.. flash

        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");

        assertThat(vitriol.getNickname()).isEqualTo("vitriol951");
        assertThat(vitriol.getBio()).isEqualTo("bio");
        assertThat(vitriol.getModifiedDate()).isAfter(vitriol.getCreatedDate());
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("회원 정보 변경 - 정상 - 닉네임 그대로")
    @Test
    void 회원_정보_변경_닉네임_그대로() throws Exception {

        mockMvc.perform(post("/settings")
                .param("nickname", "vitriol95")
                .param("bio", "bio")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings"))
                .andExpect(view().name("redirect:/settings"))
                .andExpect(flash().attributeExists("message"));
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("회원 정보 변경 - 실패 - 중복 닉네임")
    @Test
    void 회원_정보_변경_실패_중복_닉네임() throws Exception {

        mockMvc.perform(post("/settings")
                .param("nickname", "test")
                .param("bio", "bio")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/settings"));
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("회원 정보 변경 - 입력값 오류")
    @Test
    void 회원_정보_변경_입력값_오류() throws Exception {

        mockMvc.perform(post("/settings")
                .param("nickname", "vitriol95vitriol95vitriol95itriol95itriol95itriol95itriol95itriol95")
                .param("bio", "bio")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/settings"));
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("회원 전체 검색")
    @Test
    void 회원_전체_검색() throws Exception {

        for (int i = 1; i <= 41; i++) {
            accountFactory.newAccount("testAccount" + i);
        } // 총 41 + 2 유저가 존재. 3페이지 분량 (20, 20, 3)

        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/all"))
                .andExpect(model().attributeExists("accountPage"))
                .andExpect(model().attribute("accountPage", IsIterableWithSize.iterableWithSize(20)));

        mockMvc.perform(get("/accounts?page=1"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/all"))
                .andExpect(model().attributeExists("accountPage"))
                .andExpect(model().attribute("accountPage", IsIterableWithSize.iterableWithSize(20)));

        mockMvc.perform(get("/accounts?page=2"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/all"))
                .andExpect(model().attributeExists("accountPage"))
                .andExpect(model().attribute("accountPage", IsIterableWithSize.iterableWithSize(3)));

        assertThat(accountRepository.count()).isEqualTo(43L);

    }
}