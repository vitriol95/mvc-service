package vitriol.mvcservice.modules.reply;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vitriol.mvcservice.modules.account.Account;
import vitriol.mvcservice.modules.post.Post;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findByPost(Post post);

    @EntityGraph(attributePaths = "account")
    Reply findReplyById(Long id);

    @Query("select r from Reply r where r.account in (:accounts)")
    List<Reply> findReplyByAccount(@Param("accounts") Set<Account> accounts);
}
