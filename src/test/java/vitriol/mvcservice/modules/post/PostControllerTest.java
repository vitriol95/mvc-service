package vitriol.mvcservice.modules.post;

import org.assertj.core.api.Assertions;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import vitriol.mvcservice.modules.account.Account;
import vitriol.mvcservice.modules.account.AccountRepository;
import vitriol.mvcservice.modules.account.AccountService;
import vitriol.mvcservice.modules.account.form.SignUpForm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class PostControllerTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("vitriol95@naver.com");
        signUpForm.setNickname("vitriol95");
        signUpForm.setPassword("12345678");
        accountService.createNewAccount(signUpForm);

        SignUpForm signUpForm2 = new SignUpForm();
        signUpForm2.setEmail("vitriol95@gmail.com");
        signUpForm2.setNickname("vitriol951");
        signUpForm2.setPassword("12345678");
        accountService.createNewAccount(signUpForm2);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("포스트 작성 뷰")
    @Test
    void 포스트_작성_뷰() throws Exception {

        mockMvc.perform(get("/new-post"))
                .andExpect(view().name("post/form"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("newPostForm"));

    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("포스트 작성 - 입력값 정상")
    @Test
    void 포스트_작성_입력값_정상() throws Exception {

        mockMvc.perform(post("/new-post")
                .param("title", "TestTitle")
                .param("introduction", "TestIntroduction")
                .param("description", "TestDescription")
                .param("open", String.valueOf(true))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));

        Post post = postRepository.findPostWithAccountById(1L);
        assertThat(post).isNotNull();
        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");
        assertThat(vitriol.getPosts()).contains(post); // account 내에 저장되었는가?
        assertThat(post.isWriter(vitriol)).isTrue(); // 글쓴이가 맞는가?
        assertThat(post.getId()).isEqualTo(1L);
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("포스트 작성 - 입력값 오류")
    @Test
    void 포스트_작성_입력값_오류() throws Exception {

        mockMvc.perform(post("/new-post")
                .param("title", "TestTitle")
                .param("introduction", "TestIntroduction")
                .param("description", "") // 본문이 없음
                .param("open", String.valueOf(true))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("post/form"));

        Post post = postRepository.findPostWithAccountById(1L);
        assertThat(post).isNull();
        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");
        assertThat(vitriol.getPosts()).doesNotContain(post);
    }


    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("포스트 상세보기 뷰 - 글쓴이가 봤을 때")
    @Test
    void 포스트_상세보기_뷰_글쓴이가_봤을_때() throws Exception {

        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");
        new_post(true, vitriol);

        MvcResult result = mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("post"))
                .andExpect(content().string())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThat(body).contains("수정");
        assertThat(body).contains("삭제");
        // TODO: 나중에 Reply 추가 되면 이 테스트또한 진행해 보아야함. + 비밀글 테스트 추가해야함
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("포스트 상세보기 뷰 - 글쓴이 아닌 사람이 봤을 때")
    @Test

    private void new_post(boolean open, Account account) {
        Post post = new Post();
        post.setWriter(account);
        post.setDescription("description");
        post.setIntroduction("introduction");
        post.setOpen(open);
        post.setTitle("title");
        postService.createNewPost(post, account);
    }

}