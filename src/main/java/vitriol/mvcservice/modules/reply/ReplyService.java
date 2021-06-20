package vitriol.mvcservice.modules.reply;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vitriol.mvcservice.modules.account.Account;
import vitriol.mvcservice.modules.account.AccountRepository;
import vitriol.mvcservice.modules.post.Post;

@Transactional
@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final AccountRepository accountRepository;

    public Reply createNewReply(Reply reply, Post post, Account account) {
        Account writer = accountRepository.findByEmail(account.getEmail());
        // detached 살려오기

        reply.postedOn(post);
        reply.setWriter(writer);
        return replyRepository.save(reply);
    }
}
