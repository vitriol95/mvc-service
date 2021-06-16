package vitriol.mvcservice.modules.post;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long> {

    // TODO: Reply 까지 Fetch 해야함
    Post findPostWithUserAndRepliesById(Long id);

    @EntityGraph(attributePaths = "account")
    Post findPostWithAccountById(Long id);
}
