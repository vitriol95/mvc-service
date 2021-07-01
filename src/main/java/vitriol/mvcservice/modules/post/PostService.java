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
import java.util.*;
import java.util.stream.Collectors;

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

        newPost.setWriter(writer);
        return postRepository.save(newPost);
    }

    public Post getVanillaPost(Long id) {
        return postRepository.findPostById(id);
    }

    public Post getPostToUpdate(Long id, Account account) {
        Post post = postRepository.findPostWithAccountById(id);
        validateWriter(account, post);
        return post;
    }

    public Post getPostToDelete(Long id, Account account) {
        Post post = postRepository.findDeletePostWithAccountAndRepliesById(id);
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

    public void deletePost(Post post) {
        post.unsetWriter(post.getAccount());
        List<Reply> targetReplies = post.getReplies();

        replyRepository.bulkDeleteByRemovePost(targetReplies.stream().map(Reply::getId).collect(Collectors.toSet()));
        post.makeRepliesEmpty();
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

        reply.depostedOn(post);
        replyRepository.delete(reply);
    }
}
