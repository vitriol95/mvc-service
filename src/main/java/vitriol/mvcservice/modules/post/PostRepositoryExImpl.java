package vitriol.mvcservice.modules.post;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import vitriol.mvcservice.modules.account.QAccount;

public class PostRepositoryExImpl extends QuerydslRepositorySupport implements PostRepositoryEx {

    public PostRepositoryExImpl() {
        super(Post.class);
    }

    @Override
    public Page<Post> findByKeyword(String keyword, Pageable pageable) {
        QPost post = QPost.post;
        JPQLQuery<Post> query = from(post).where(post.open.isTrue()
                .and(post.title.containsIgnoreCase(keyword)))
                .leftJoin(post.account, QAccount.account).fetchJoin();

        JPQLQuery<Post> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Post> fetchResults = pageableQuery.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }
//
//    @Override
//    public Post findPostWithUserAndRepliesById(Long id) {
//
//    }
}
