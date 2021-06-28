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

    public void removeTrace() {
        this.account.replyRemove();
    }
}
