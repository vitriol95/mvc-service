package vitriol.mvcservice.modules.reply;

import lombok.*;
import vitriol.mvcservice.modules.account.Account;
import vitriol.mvcservice.modules.account.UserAccount;
import vitriol.mvcservice.modules.post.Post;
import vitriol.mvcservice.modules.superclass.LocalDateTimeEntity;

import javax.persistence.*;

@Entity
@Table(name = "reply")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Reply extends LocalDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Lob
    private String description;

    public boolean isWriter(UserAccount userAccount) {
        return this.account.equals(userAccount.getAccount());
    }

    /** 양방향 매핑 메서드 With Account */
    public void setWriter(Account account) {
        this.account = account;
        account.replyAdd(this);
    }
    /** 양방향 매핑 메서드 With Account */
    public void unsetWriter(Account account) {
        account.replyRemove(this);
    }

    /** 양방향 매핑 메서드 With Post */
    public void postedOn(Post post) {
        this.post = post;
        post.replyAdd(this);
    }
    /** 양방향 매핑 메서드 With Post */
    public void depostedOn(Post post) {
        post.replyRemove(this);
    }
}
