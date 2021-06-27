package vitriol.mvcservice.modules.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface PostRepositoryEx {

    Page<Post> findByKeyword(String keyword, Pageable pageable);

}
