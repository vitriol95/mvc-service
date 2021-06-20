package vitriol.mvcservice.modules.reply.form;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Lob;


@Data
public class NewReplyForm {

    @Lob
    @Length(min = 2)
    private String description;
}
