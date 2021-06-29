package vitriol.mvcservice.modules.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import vitriol.mvcservice.modules.account.Account;
import vitriol.mvcservice.modules.account.AccountFactory;
import vitriol.mvcservice.modules.account.AccountRepository;
import vitriol.mvcservice.modules.account.AccountService;
import vitriol.mvcservice.modules.account.form.SignUpForm;
import vitriol.mvcservice.modules.reply.Reply;
import vitriol.mvcservice.modules.reply.ReplyRepository;
import vitriol.mvcservice.modules.reply.form.NewReplyForm;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class PostWithReplyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

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

    @DisplayName("댓글 입력 폼")
    @Test
    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void 댓글_입력_폼() throws Exception {

        Account account = accountFactory.newAccount("test1");
        Post post = postFactory.newPost("title", account);
        Long postId = post.getId();

        mockMvc.perform(get("/posts/" + postId))
                .andExpect(view().name("post/view"))
                .andExpect(model().attributeExists("newReplyForm"))
                .andExpect(status().isOk());
    }

    @DisplayName("댓글 작성")
    @Test
    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void 댓글_작성() throws Exception {

        Account account = accountFactory.newAccount("test1");
        Post post2 = postFactory.newPost("title", account);
        Long postId = post2.getId();

        NewReplyForm replyForm = new NewReplyForm();
        replyForm.setDescription("aaa");

        // Ajax
        mockMvc.perform(post("/posts/" + postId + "/reply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(replyForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Reply reply = replyRepository.findByPost(post2).get(0);

        assertThat(reply).isNotNull();
        assertThat(reply.getAccount().getEmail()).isEqualTo("vitriol95@naver.com");
        assertThat(reply.getDescription()).isEqualTo("aaa");
        assertThat(reply.getPost()).isEqualTo(post2);
        assertThat(post2.getReplies()).contains(reply);
        assertThat(post2.getReplyCount()).isEqualTo(1L);

        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");
        assertThat(vitriol.getReplyCount()).isEqualTo(1L);
    }

    @DisplayName("입력된 댓글 뷰")
    @Test
    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void 입력된_댓글_뷰() throws Exception {

        Account test1 = accountFactory.newAccount("test1");
        Account test2 = accountFactory.newAccount("test2");
        Post post1 = postFactory.newPost("test1's post", test1);
        Reply reply1 = replyFactory.newReply(post1, test1);
        Reply reply2 = replyFactory.newReply(post1, test2);

        MvcResult mvcResult = mockMvc.perform(get("/posts/" + post1.getId()))
                .andExpect(view().name("post/view"))
                .andExpect(model().attributeExists("newReplyForm"))
                .andExpect(status().isOk()).andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        assertThat(content).contains(reply1.getDescription());
        assertThat(content).contains(reply1.getAccount().getNickname());
        assertThat(content).contains(reply1.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        assertThat(content).contains(reply2.getDescription());
        assertThat(content).contains(reply2.getAccount().getNickname());
        assertThat(content).contains(reply2.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        assertThat(content).doesNotContain("삭제");
    }

    @DisplayName("댓글 삭제")
    @Test
    @WithUserDetails(value = "vitriol95@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void 댓글_삭제() throws Exception {
        Account test1 = accountFactory.newAccount("test1");
        Post post1 = postFactory.newPost("title112", test1);

        Account vitriol = accountRepository.findByEmail("vitriol95@naver.com");
        Reply reply1 = replyFactory.newReply(post1, vitriol);

        mockMvc.perform(post("/posts/" + post1.getId() + "/reply/" + reply1.getId() + "/delete")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + post1.getId()));

        Reply replyById = replyRepository.findReplyById(reply1.getId());
        assertThat(replyById).isNull();
        assertThat(post1.getReplies()).doesNotContain(reply1);
        assertThat(post1.getReplyCount()).isEqualTo(0L);
        assertThat(vitriol.getReplies()).doesNotContain(reply1);
        assertThat(vitriol.getReplyCount()).isEqualTo(0L);
    }
}
