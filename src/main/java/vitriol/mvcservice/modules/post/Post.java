package vitriol.mvcservice.modules.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vitriol.mvcservice.modules.account.Account;
import vitriol.mvcservice.modules.account.UserAccount;
import vitriol.mvcservice.modules.superclass.LocalDateTimeEntity;

import javax.persistence.*;

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

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String description;

    private boolean open;

    public void setWriter(Account account) {
        this.account = account;
        account.getPosts().add(this);
    }

    public boolean isWriter(UserAccount userAccount) {
        return this.account.equals(userAccount.getAccount());
    }

    public boolean isWriter(Account account) {
        return this.account.equals(account);
    }

}
