package vitriol.mvcservice.modules.post;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vitriol.mvcservice.modules.account.Account;
import vitriol.mvcservice.modules.reply.Reply;


@Component
@RequiredArgsConstructor
public class ReplyFactory {

    @Autowired
    PostService postService;

    public Reply newReply(Post post, Account account) {
        Reply reply = new Reply();
        String randomString = RandomString.make(10);
        reply.setDescription(randomString);
        postService.createNewReply(reply, post, account);
        return reply;
    }
}
