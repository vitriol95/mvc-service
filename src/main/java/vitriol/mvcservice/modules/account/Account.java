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
    @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    @OneToMany(mappedBy = "account")
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "account")
    private List<Reply> replies = new ArrayList<>();

}
