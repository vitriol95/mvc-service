package vitriol.mvcservice.modules.post;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import vitriol.mvcservice.modules.account.Account;
import vitriol.mvcservice.modules.account.AccountRepository;
import vitriol.mvcservice.modules.post.form.NewPostForm;
import vitriol.mvcservice.modules.reply.Reply;
import vitriol.mvcservice.modules.reply.ReplyRepository;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final ReplyRepository replyRepository;

    public Post createNewPost(Post newPost, Account account) {

        Account writer = accountRepository.findByEmail(account.getEmail());
        // Detached 상태인 애를 데려오기

        Post post = postRepository.save(newPost);
        post.setWriter(writer);
        return post;
    }

    public Post getVanillaPost(Long id) {
        return postRepository.findPostById(id);
    }

    public Post getPostToUpdate(Long id, Account account) {
        Post post = postRepository.findPostWithAccountById(id);
        validateWriter(account, post);
        return post;
    }

    private void validateWriter(Account account, Post post) {
        if (!post.isWriter(account)) {
            throw new AccessDeniedException("작성자가 아닙니다.");
        }
    }

    public void updatePost(Post post, NewPostForm newPostForm) {
        modelMapper.map(newPostForm, post);
    }

    public void deletePost(Post post, Account account) {
        Account writer = accountRepository.findByEmail(account.getEmail());
        // Detached

        post.unsetWriter(writer);
        postRepository.delete(post);
    }

    public void createNewReply(Reply reply, Post post, Account account) {
        Account writer = accountRepository.findByEmail(account.getEmail());
        // detached 살려오기

        reply.setWriter(writer);
        reply.postedOn(post);
        replyRepository.save(reply);
    }

    public void deleteReply(Reply reply, Post post) {
//        Account writer = accountRepository.findByEmail(account.getEmail());
        // 여기서 Detached 를 다시 가져올 필요가 없다. 왜? --> Reply는 Account를 Eager Fetch

        reply.unsetWriter();
        reply.depostedOn(post);
        replyRepository.delete(reply);
    }
}
