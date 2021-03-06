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

    @DisplayName("???????????? ???")
    @Test
    void ????????????_???() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated());
    }

    @DisplayName("???????????? ??????")
    @Test
    void ????????????_??????() throws Exception {
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

    @DisplayName("???????????? ?????? - ?????? ???????????? ?????????")
    @Test
    void ????????????_??????_??????_????????????_?????????() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("email", "vitriol95@naver.com")
                .param("nickname", "vitriol123")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("???????????? ?????? - ?????? ???????????? ?????????")
    @Test
    void ????????????_??????_??????_????????????_?????????() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("email", "vitriol951@naver.com")
                .param("nickname", "vitriol95")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("???????????? ?????? - ????????? ????????????")
    @Test
    void ????????????_??????_?????????_????????????() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("email", "vitriol951@naver.com")
                .param("nickname", "vitriol")
                .param("password", "12")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    // ------ ?????? ?????? ------ //
    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("?????? ?????? ?????? ???")
    @Test
    void ??????_??????_??????_???() throws Exception {

        mockMvc.perform(get("/settings"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/settings"))
                .andExpect(model().attributeExists("profileForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(authenticated());
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("?????? ?????? ?????? - ????????? ??????")
    @Test
    void ??????_??????_??????_?????????_??????() throws Exception {

        mockMvc.perform(post("/settings")
                .param("nickname", "vitriol951")
                .param("bio", "bio")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings"))
                .andExpect(view().name("redirect:/settings"))
                .andExpect(flash().attributeExists("message"));
        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");

        assertThat(vitriol.getNickname()).isEqualTo("vitriol951");
        assertThat(vitriol.getBio()).isEqualTo("bio");
        assertThat(vitriol.getModifiedDate()).isAfter(vitriol.getCreatedDate());
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("?????? ?????? ?????? - ?????? - ????????? ?????????")
    @Test
    void ??????_??????_??????_?????????_?????????() throws Exception {

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
    @DisplayName("?????? ?????? ?????? - ?????? - ?????? ?????????")
    @Test
    void ??????_??????_??????_??????_??????_?????????() throws Exception {

        mockMvc.perform(post("/settings")
                .param("nickname", "test")
                .param("bio", "bio")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/settings"));
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("?????? ?????? ?????? - ????????? ??????")
    @Test
    void ??????_??????_??????_?????????_??????() throws Exception {

        mockMvc.perform(post("/settings")
                .param("nickname", "vitriol95vitriol95vitriol95itriol95itriol95itriol95itriol95itriol95")
                .param("bio", "bio")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/settings"));
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("?????? ?????? ??????")
    @Test
    void ??????_??????_??????() throws Exception {

        for (int i = 1; i <= 41; i++) {
            accountFactory.newAccount("testAccount" + i);
        } // ??? 41 + 2 ????????? ??????. 3????????? ?????? (20, 20, 3)

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