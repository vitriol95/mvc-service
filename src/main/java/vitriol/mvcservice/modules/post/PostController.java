package vitriol.mvcservice.modules.post;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import vitriol.mvcservice.modules.account.Account;
import vitriol.mvcservice.modules.account.LoggedInUser;
import vitriol.mvcservice.modules.post.form.NewPostForm;
import vitriol.mvcservice.modules.reply.Reply;
import vitriol.mvcservice.modules.reply.ReplyRepository;
import vitriol.mvcservice.modules.reply.ReplyService;
import vitriol.mvcservice.modules.reply.form.NewReplyForm;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ModelMapper modelMapper;
    private final PostRepository postRepository;
    private final ReplyService replyService;
    private final ReplyRepository replyRepository;

    @GetMapping("/new-post")
    public String newPostForm(@LoggedInUser Account account, Model model) {

        model.addAttribute(account);
        model.addAttribute(new NewPostForm());
        return "post/form";
    }

    @PostMapping("/new-post")
    public String postFormSubmit(@LoggedInUser Account account, Model model, @Valid NewPostForm newPostForm, Errors errors) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "post/form";
        }
        Post post = postService.createNewPost(modelMapper.map(newPostForm, Post.class), account);
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/posts/{id}")
    public String postView(@LoggedInUser Account account, @PathVariable("id") Long id, Model model) {
        Post post = postRepository.findPostWithUserAndRepliesById(id);
        if (!post.isOpen()) {
            model.addAttribute(account);
            return "redirect:/";
        }
        model.addAttribute(account);
        model.addAttribute("post", post);
        model.addAttribute(new NewReplyForm());
        return "post/view";
    }

    @PostMapping(value = "/posts/{id}/reply")
    @ResponseBody
    public ResponseEntity replyFormSubmit(@PathVariable Long id, @LoggedInUser Account account, @RequestBody NewReplyForm newReplyForm) {
        Post post = postService.getVanillaPost(id);
        postService.createNewReply(modelMapper.map(newReplyForm, Reply.class), post, account);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/posts/{postId}/reply/{replyId}/delete")
    public String deleteReplySubmit(@PathVariable("postId") Long id, @PathVariable("replyId") Long replyId) {
        Post post = postService.getVanillaPost(id);
        Reply reply = replyRepository.findReplyById(replyId); // Account 까지 Fetch 된상태에 해당한다.
        postService.deleteReply(reply, post);
        return "redirect:/posts/" + id;
    }

    @GetMapping("/posts/{id}/update")
    public String updatePostFormView(@LoggedInUser Account account, @PathVariable Long id, Model model) {
        Post post = postService.getPostToUpdate(id, account);
        model.addAttribute(account);
        NewPostForm map = modelMapper.map(post, NewPostForm.class);
        model.addAttribute("newPostForm", map);
        return "post/update";
    }

    @PostMapping("/posts/{id}/update")
    public String updatePostFormSubmit(@LoggedInUser Account account, @PathVariable Long id, Model model, @Valid NewPostForm newPostForm, Errors errors) {

        Post post = postService.getPostToUpdate(id, account);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "post/update";
        }

        model.addAttribute(account);
        postService.updatePost(post, newPostForm);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePostSubmit(@LoggedInUser Account account, @PathVariable Long id) {
        Post post = postService.getPostToUpdate(id, account);
        postService.deletePost(post, account);
        return "redirect:/";
    }
}
