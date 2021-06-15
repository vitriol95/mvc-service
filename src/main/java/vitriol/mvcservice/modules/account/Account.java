package vitriol.mvcservice.modules.account;
import lombok.*;
import vitriol.mvcservice.modules.superclass.LocalDateTimeEntity;
import javax.persistence.*;

@Entity
@Table(name = "account")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Account extends LocalDateTimeEntity {

    @Id
    @GeneratedValue
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


}
