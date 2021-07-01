package vitriol.mvcservice.modules.account;
import lombok.*;
import vitriol.mvcservice.modules.superclass.LocalDateTimeEntity;
import javax.persistence.*;

@Entity
@Table(name = "account")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id",callSuper = false)
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

    private Long postCount;

    public void plusPostCount() {
        this.postCount = this.postCount == null ? 1L : this.postCount + 1;
    }

    public void minusPostCount() {
        this.postCount--;
    }
}
