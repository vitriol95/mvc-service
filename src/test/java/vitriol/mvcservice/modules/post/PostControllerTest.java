package vitriol.mvcservice.modules.post;

import org.apache.tomcat.jni.Local;
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
import org.springframework.transaction.annotation.Transactional;
import vitriol.mvcservice.modules.account.Account;
import vitriol.mvcservice.modules.account.AccountRepository;
import vitriol.mvcservice.modules.account.AccountService;
import vitriol.mvcservice.modules.account.form.SignUpForm;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        postRepository.deleteAll();
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

        MvcResult mvcResult = mockMvc.perform(post("/new-post")
                .param("title", "TestTitle")
                .param("introduction", "TestIntroduction")
                .param("description", "TestDescription")
                .param("open", String.valueOf(true))
                .with(csrf()))
                .andExpect(status().is3xxRedirection()).andReturn();

        String[] url = mvcResult.getResponse().getRedirectedUrl().split("/");
        Long id = Long.parseLong(url[url.length - 1]); // redirected id 가져오기

        Post post = postRepository.findPostWithAccountById(id);
        assertThat(post).isNotNull();
        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");
        assertThat(vitriol.getPosts()).contains(post); // account 내에 저장되었는가?
        assertThat(post.isWriter(vitriol)).isTrue(); // 글쓴이가 맞는가?
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
        Long id = new_post(true, vitriol);

        MvcResult result = mockMvc.perform(get("/posts/"+id))
                .andExpect(status().isOk())
                .andExpect(view().name("post/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("post"))
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThat(body).contains("수정");
        assertThat(body).contains("삭제");
        // XPath로 바꾸어 검증해보는 건?
    }

    @WithUserDetails(value = "vitriol95@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("포스트 상세보기 뷰 - 글쓴이 아닌 사람이 봤을 때")
    @Test
    void 포스트_상세보기_뷰_글쓴이_아닌_사람이_봤을_때() throws Exception {
        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");
        Long id = new_post(true, vitriol);

        MvcResult result = mockMvc.perform(get("/posts/"+id))
                .andExpect(status().isOk())
                .andExpect(view().name("post/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("post"))
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThat(body).doesNotContain("수정");
        assertThat(body).doesNotContain("삭제");
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("포스트 업데이트 뷰 - 글쓴이가 봤을 때")
    @Test
    void 포스트_업데이트_뷰_글쓴이() throws Exception {
        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");
        Long id = new_post(true, vitriol);

        mockMvc.perform(get("/posts/" + id + "/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/update"))
                .andExpect(model().attributeExists("newPostForm"))
                .andExpect(model().attributeExists("account"));
    }

    @WithUserDetails(value = "vitriol95@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("포스트 상세보기 뷰 - 글쓴이 아닌 사람이 접근불가")
    @Test
    void 포스트_업데이트_뷰_글쓴이_아닌_사람_접근불가() throws Exception {
        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");
        Long id = new_post(true, vitriol);

        mockMvc.perform(get("/posts/" + id + "/update"))
                .andExpect(status().is4xxClientError());
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("포스트 업데이트 - 입력값 정상")
    @Test
    void 포스트_업데이트_입력값_정상() throws Exception {

        Long id = vitriol95Post();
        mockMvc.perform(post("/posts/" + id + "/update")
                .param("title", "newTestTitle")
                .param("introduction", "newIntroduction")
                .param("description", "description")
                .param("open", String.valueOf(true))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/posts/" + id));

        Post post = postRepository.findPostWithAccountById(id);
        assertThat(post).isNotNull();
        assertThat(post.getAccount().getEmail()).isEqualTo("vitriol95@naver.com");
        assertThat(post.getTitle()).isEqualTo("newTestTitle");
        assertThat(post.getIntroduction()).isEqualTo("newIntroduction");
        assertThat(post.getDescription()).isEqualTo("description");
        assertThat(post.isOpen()).isTrue();
        assertThat(post.getModifiedDate()).isAfter(post.getCreatedDate());
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("포스트 업데이트 - 입력값 오류")
    @Test
    void 포스트_업데이트_입력값_오류() throws Exception {

        Long id = vitriol95Post();

        mockMvc.perform(post("/posts/" + id + "/update")
                .param("title", "50글자넘기면안됨50글자넘기면안됨50글자넘기면안됨50글자넘기면안됨50글자넘기면안됨50글자넘기면안됨50글자넘기면안됨50글자넘기면안됨50글자넘기면안됨50글자넘기면안됨50글자넘기면안됨50글자넘기면안됨")
                .param("introduction", "intro")
                .param("description", "description")
                .param("open", String.valueOf(true))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("post/update"));

    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("포스트 제거")
    @Test
    void 포스트_제거() throws Exception {

        Long id = vitriol95Post();

        mockMvc.perform(post("/posts/" + id + "/delete")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        Post post = postRepository.findPostWithAccountById(id);
        assertThat(post).isNull();
    }

    private Long vitriol95Post() {

        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");
        return new_post(true, vitriol);
    }

    private Long new_post(boolean open, Account account) {
        Post post = new Post();
        post.setWriter(account);
        post.setDescription("description");
        post.setIntroduction("introduction");
        post.setOpen(open);
        post.setTitle("title");
        Post newPost = postService.createNewPost(post, account);
        return newPost.getId();
    }

}