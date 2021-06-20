package vitriol.mvcservice.modules.reply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import vitriol.mvcservice.modules.post.Post;

import java.util.List;

@Transactional(readOnly = true)
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findByPost(Post post);
}
