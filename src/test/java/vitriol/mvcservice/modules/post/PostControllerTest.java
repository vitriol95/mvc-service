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
    @DisplayName("????????? ?????? ???")
    @Test
    void ?????????_??????_???() throws Exception {

        mockMvc.perform(get("/new-post"))
                .andExpect(view().name("post/form"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("newPostForm"));

    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ?????? - ????????? ??????")
    @Test
    void ?????????_??????_?????????_??????() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/new-post")
                .param("title", "TestTitle")
                .param("introduction", "TestIntroduction")
                .param("description", "TestDescription")
                .param("open", String.valueOf(true))
                .with(csrf()))
                .andExpect(status().is3xxRedirection()).andReturn();

        String[] url = mvcResult.getResponse().getRedirectedUrl().split("/");
        Long id = Long.parseLong(url[url.length - 1]); // redirected id ????????????

        Post post = postRepository.findPostWithAccountById(id);
        assertThat(post).isNotNull();
        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");
        assertThat(vitriol.getPostCount()).isEqualTo(1L);
        assertThat(post.isWriter(vitriol)).isTrue(); // ???????????? ??????????
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ?????? - ????????? ??????")
    @Test
    void ?????????_??????_?????????_??????() throws Exception {

        mockMvc.perform(post("/new-post")
                .param("title", "TestTitle")
                .param("introduction", "TestIntroduction")
                .param("description", "") // ????????? ??????
                .param("open", String.valueOf(true))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("post/form"));

        Post post = postRepository.findPostWithAccountById(1L);
        assertThat(post).isNull();
        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");
        assertThat(vitriol.getPostCount()).isEqualTo(0);
    }


    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ???????????? ??? - ???????????? ?????? ???")
    @Test
    void ?????????_????????????_???_????????????_??????_???() throws Exception {

        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");
        Long id = new_post(true, vitriol);

        MvcResult result = mockMvc.perform(get("/posts/"+id))
                .andExpect(status().isOk())
                .andExpect(view().name("post/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("post"))
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThat(body).contains("??????");
        assertThat(body).contains("??????");
        // XPath??? ????????? ??????????????? ????
    }

    @WithUserDetails(value = "vitriol95@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ???????????? ??? - ????????? ?????? ????????? ?????? ???")
    @Test
    void ?????????_????????????_???_?????????_??????_?????????_??????_???() throws Exception {
        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");
        Long id = new_post(true, vitriol);

        MvcResult result = mockMvc.perform(get("/posts/"+id))
                .andExpect(status().isOk())
                .andExpect(view().name("post/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("post"))
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThat(body).doesNotContain("??????");
        assertThat(body).doesNotContain("??????");
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ???????????? ??? - ???????????? ?????? ???")
    @Test
    void ?????????_????????????_???_?????????() throws Exception {
        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");
        Long id = new_post(true, vitriol);

        mockMvc.perform(get("/posts/" + id + "/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/update"))
                .andExpect(model().attributeExists("newPostForm"))
                .andExpect(model().attributeExists("account"));
    }

    @WithUserDetails(value = "vitriol95@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ???????????? ??? - ????????? ?????? ????????? ????????????")
    @Test
    void ?????????_????????????_???_?????????_??????_??????_????????????() throws Exception {
        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");
        Long id = new_post(true, vitriol);

        mockMvc.perform(get("/posts/" + id + "/update"))
                .andExpect(status().is4xxClientError());
    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ???????????? - ????????? ??????")
    @Test
    void ?????????_????????????_?????????_??????() throws Exception {

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
    @DisplayName("????????? ???????????? - ????????? ??????")
    @Test
    void ?????????_????????????_?????????_??????() throws Exception {

        Long id = vitriol95Post();

        mockMvc.perform(post("/posts/" + id + "/update")
                .param("title", "50?????????????????????50?????????????????????50?????????????????????50?????????????????????50?????????????????????50?????????????????????50?????????????????????50?????????????????????50?????????????????????50?????????????????????50?????????????????????50?????????????????????")
                .param("introduction", "intro")
                .param("description", "description")
                .param("open", String.valueOf(true))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("post/update"));

    }

    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ??????")
    @Test
    void ?????????_??????() throws Exception {

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