package vitriol.mvcservice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vitriol.mvcservice.modules.account.Account;
import vitriol.mvcservice.modules.account.LoggedInUser;
import vitriol.mvcservice.modules.post.Post;
import vitriol.mvcservice.modules.post.PostRepository;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final PostRepository postRepository;

    @GetMapping("/")
    public String main(@LoggedInUser Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }
        model.addAttribute("postList", postRepository.findFirst12ByOpenOrderByCreatedDateDesc(true));
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/search/post")
    public String searchPost(@PageableDefault(size = 12, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable, String keyword, Model model) {
        Page<Post> postPage = postRepository.findByKeyword(keyword, pageable);
        model.addAttribute("postPage", postPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("createdDate") ? "createdDate" : "replyCount");

        return "search";
    }
}
