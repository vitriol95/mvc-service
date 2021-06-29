package vitriol.mvcservice.modules.post;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vitriol.mvcservice.modules.account.Account;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class PostFactory {

    @Autowired
    PostService postService;

    public Post newPost(String title, Account writer) {


        Post post = new Post();
        post.setTitle(title);
        post.setIntroduction("test");
        post.setDescription("description");
        post.setOpen(true);
        postService.createNewPost(post, writer);
        return post;
    }
}
