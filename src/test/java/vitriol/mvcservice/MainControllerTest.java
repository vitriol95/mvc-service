package vitriol.mvcservice;

import org.assertj.core.api.Assertions;
import org.hamcrest.collection.IsIterableWithSize;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import vitriol.mvcservice.modules.account.Account;
import vitriol.mvcservice.modules.account.AccountFactory;
import vitriol.mvcservice.modules.account.AccountRepository;
import vitriol.mvcservice.modules.account.AccountService;
import vitriol.mvcservice.modules.account.form.SignUpForm;
import vitriol.mvcservice.modules.post.*;
import vitriol.mvcservice.modules.reply.ReplyRepository;

import javax.transaction.Transactional;

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
class MainControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostService postService;

    @Autowired
    AccountFactory accountFactory;

    @Autowired
    PostFactory postFactory;

    @Autowired
    ReplyFactory replyFactory;

    @Autowired
    ReplyRepository replyRepository;

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
        postRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @DisplayName("메인 화면")
    @Test
    void 메인화면() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(unauthenticated());
    }

    @DisplayName("로그인 성공")
    @Test
    void 로그인_성공() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "vitriol95@naver.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("vitriol95@naver.com"));
    }

    @DisplayName("로그인 실패 - 없는 이메일")
    @Test
    void 로그인_실패_없는_이메일() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "vitriol9@naver.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("로그인 실패 - 비밀번호 오류")
    @Test
    void 로그인_실패_비밀번호_오류() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "vitriol95@naver.com")
                .param("password", "123456789")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("게시글 검색 - 전체 검색")
    @Test
    void 게시글_검색_전체_검색() throws Exception {

        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");
        Account creator = accountFactory.newAccount("creator");
        for (int i = 1; i <= 14; i++) {
            Post withR = postFactory.newPost("testTitle" + i, creator);
            if (i == 13) {
                replyFactory.newReply(withR, vitriol);
            }
        }

        mockMvc.perform(get("/search/post")
                .param("keyword", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("postPage"))
                .andExpect(model().attributeExists("keyword"))
                .andExpect(model().attributeExists("sortProperty"))
                .andExpect(model().attribute("keyword", ""))
                .andExpect(model().attribute("postPage", IsIterableWithSize.iterableWithSize(12)));

        mockMvc.perform(get("/search/post")
                .param("keyword", "")
                .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("postPage"))
                .andExpect(model().attributeExists("keyword"))
                .andExpect(model().attributeExists("sortProperty"))
                .andExpect(model().attribute("keyword", ""))
                .andExpect(model().attribute("postPage", IsIterableWithSize.iterableWithSize(2)));

        // 정렬 방식
        mockMvc.perform(get("/search/post")
                .param("sort", "createdDate,desc")
                .param("keyword", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("postPage"))
                .andExpect(model().attributeExists("keyword"))
                .andExpect(model().attributeExists("sortProperty"))
                .andExpect(model().attribute("keyword", ""))
                .andExpect(model().attribute("postPage", IsIterableWithSize.iterableWithSize(12)))
                .andExpect(model().attribute("sortProperty", "createdDate"));

        mockMvc.perform(get("/search/post")
                .param("sort", "replyCount,desc")
                .param("keyword", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("postPage"))
                .andExpect(model().attributeExists("keyword"))
                .andExpect(model().attributeExists("sortProperty"))
                .andExpect(model().attribute("keyword", ""))
                .andExpect(model().attribute("postPage", IsIterableWithSize.iterableWithSize(12)))
                .andExpect(model().attribute("sortProperty", "replyCount"));

        assertThat(postRepository.count()).isEqualTo(14L);
        assertThat(replyRepository.count()).isEqualTo(1L);
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("게시글 검색 - 특정 검색어")
    @Test
    void 게시글_검색_특정_검색어() throws Exception {

        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");
        Account creator = accountFactory.newAccount("creator");

        for (int i = 1; i <= 10; i++) {
            postFactory.newPost("testTitle" + i, creator);
        }
        for (int i = 1; i <= 10; i++) {
            postFactory.newPost("vitriol", vitriol);
        }


        mockMvc.perform(get("/search/post")
                .param("keyword", "vitriol"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("postPage"))
                .andExpect(model().attributeExists("keyword"))
                .andExpect(model().attributeExists("sortProperty"))
                .andExpect(model().attribute("keyword", "vitriol"))
                .andExpect(model().attribute("postPage", IsIterableWithSize.iterableWithSize(10)));

    }
}