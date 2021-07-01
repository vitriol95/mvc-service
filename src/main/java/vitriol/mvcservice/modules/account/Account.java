package vitriol.mvcservice.modules.account;
import lombok.*;
import vitriol.mvcservice.modules.post.Post;
import vitriol.mvcservice.modules.reply.Reply;
import vitriol.mvcservice.modules.superclass.LocalDateTimeEntity;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "account")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
@Setter
public class Account extends LocalDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;
    private String password;
    private String bio;

    @Lob
    private String profileImage;

    private Long postCount = 0L;

    public void plusPostCount() {
        this.postCount++;
    }

    public void minusPostCount() {
        this.postCount--;
    }
}
