package vitriol.mvcservice.modules.post;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import vitriol.mvcservice.modules.account.Account;
import vitriol.mvcservice.modules.account.LoggedInUser;
import vitriol.mvcservice.modules.post.form.NewPostForm;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ModelMapper modelMapper;
    private final PostRepository postRepository;

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
            // TODO 에러페이지
            model.addAttribute(account);
            return "redirect:/";
        }
        model.addAttribute("post", post);
        return "post/view";
    }

    @GetMapping("/posts/{id}/update")
    public String updatePostFormView(@LoggedInUser Account account, @PathVariable Long id, Model model) {
        Post post = postService.getPostToUpdate(id, account);
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(post, NewPostForm.class));
        return "post/update";
    }

    @PostMapping("/posts/{id}/update")
    public String updatePostFromSubmit(@LoggedInUser Account account, @PathVariable Long id, @Valid NewPostForm newPostForm, Model model, Errors errors) {
        Post post = postService.getPostToUpdate(id, account);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "posts/" + id + "/update";
        }
        postService.updatePost(post, newPostForm);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePostSubmit(@LoggedInUser Account account, @PathVariable Long id) {
        Post post = postService.getPostToUpdate(id, account);
        postService.deletePost(post);
        return "redirect:/";
    }
}
