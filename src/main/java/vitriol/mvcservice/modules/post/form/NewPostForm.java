package vitriol.mvcservice.modules.post.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class NewPostForm {

    private Long id;

    @NotBlank
    @Length(max = 50)
    private String title;

    @NotBlank
    @Length(max = 100)
    private String introduction;

    @NotBlank
    private String description;

    private boolean open;
}
