package vitriol.mvcservice.modules.account;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAccount is a Querydsl query type for Account
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAccount extends EntityPathBase<Account> {

    private static final long serialVersionUID = 779396651L;

    public static final QAccount account = new QAccount("account");

    public final vitriol.mvcservice.modules.superclass.QLocalDateTimeEntity _super = new vitriol.mvcservice.modules.superclass.QLocalDateTimeEntity(this);

    public final StringPath bio = createString("bio");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final NumberPath<Long> postCount = createNumber("postCount", Long.class);

    public final SetPath<vitriol.mvcservice.modules.post.Post, vitriol.mvcservice.modules.post.QPost> posts = this.<vitriol.mvcservice.modules.post.Post, vitriol.mvcservice.modules.post.QPost>createSet("posts", vitriol.mvcservice.modules.post.Post.class, vitriol.mvcservice.modules.post.QPost.class, PathInits.DIRECT2);

    public final StringPath profileImage = createString("profileImage");

    public final ListPath<vitriol.mvcservice.modules.reply.Reply, vitriol.mvcservice.modules.reply.QReply> replies = this.<vitriol.mvcservice.modules.reply.Reply, vitriol.mvcservice.modules.reply.QReply>createList("replies", vitriol.mvcservice.modules.reply.Reply.class, vitriol.mvcservice.modules.reply.QReply.class, PathInits.DIRECT2);

    public final NumberPath<Long> replyCount = createNumber("replyCount", Long.class);

    public QAccount(String variable) {
        super(Account.class, forVariable(variable));
    }

    public QAccount(Path<? extends Account> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAccount(PathMetadata metadata) {
        super(Account.class, metadata);
    }

}

