package vitriol.mvcservice.modules.post;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryEx {

    // TODO: Reply 내의 Account 까지 fetch 해야함 --> 필요없음. Reply내의 account는 이미 Fetch로 긁어온다.
    @EntityGraph(attributePaths = {"description","account","replies"}, type = EntityGraph.EntityGraphType.LOAD)
    Post findPostWithUserAndRepliesById(Long id);

    @EntityGraph(attributePaths = "account",type = EntityGraph.EntityGraphType.LOAD)
    Post findPostWithAccountById(Long id);

    Post findPostById(Long id);

    @EntityGraph(attributePaths = "replies", type = EntityGraph.EntityGraphType.LOAD)
    Post findPostWithRepliesById(Long id);

}
