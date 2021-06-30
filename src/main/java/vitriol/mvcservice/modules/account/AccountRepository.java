package vitriol.mvcservice.modules.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    Account findAccountById(Long id);

    Account findByEmail(String email);

    Account findByNickname(String nickname);

    @Query("select a from Account a left join fetch a.posts where a.email = :email")
    Account findAccountWithPostsByEmail(@Param("email") String email);

    @Query("update Account a set a.replyCount = a.replyCount-1 where a.id in (:ids)")
    Long updateReplyCountByRemove(@Param("ids") Set<Long> ids);
}
