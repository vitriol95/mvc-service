package vitriol.mvcservice.modules.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vitriol.mvcservice.modules.account.Account;
import vitriol.mvcservice.modules.account.UserAccount;
import vitriol.mvcservice.modules.reply.Reply;
import vitriol.mvcservice.modules.superclass.LocalDateTimeEntity;
import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Table(name = "post")
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post extends LocalDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    private String title;
    private String introduction;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    private Long replyCount = 0L;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String description;

    private boolean open;

    /** 양방향 매핑 메서드 With Post*/
    public void setWriter(Account account) {
        this.account = account;
        account.postAdd(this);
    }

    /** 양방향 매핑 메서드 With Post*/
    public void unsetWriter(Account account) {
        account.postRemove(this);
    }

    public boolean isWriter(UserAccount userAccount) {
        return this.account.equals(userAccount.getAccount());
    }

    public boolean isWriter(Account account) {
        return this.account.equals(account);
    }


    public void replyAdd(Reply reply) {
        this.getReplies().add(reply);
        this.replyCount++;
    }

    public void replyRemove(Reply reply) {
        this.getReplies().remove(reply);
        this.replyCount--;
    }
}
