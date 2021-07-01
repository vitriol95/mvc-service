package vitriol.mvcservice.modules.post;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryEx {

    @Query(value = "select distinct p from Post p join fetch p.account left join fetch p.replies where p.id = :id")
    Post findDeletePostWithAccountAndRepliesById(@Param("id") Long id);

    @Query(value = "select p from Post p join fetch p.account left join fetch p.replies r left join fetch r.account where p.id = :id")
    Post findPostWithAccountAndRepliesById(@Param("id") Long id);

    @EntityGraph(attributePaths = "account",type = EntityGraph.EntityGraphType.LOAD)
    Post findPostWithAccountById(Long id);

    Post findPostById(Long id);

    @Query(value = "select p from Post p left join fetch p.replies where p.id = :id")
    Post findPostToDeleteReplyById(@Param("id") Long id);

    @EntityGraph(attributePaths = "replies", type = EntityGraph.EntityGraphType.LOAD)
    Post findPostWithRepliesById(Long id);

}
