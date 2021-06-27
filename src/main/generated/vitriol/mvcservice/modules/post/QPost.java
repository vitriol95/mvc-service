package vitriol.mvcservice.modules.post;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = 1010027373L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final vitriol.mvcservice.modules.superclass.QLocalDateTimeEntity _super = new vitriol.mvcservice.modules.superclass.QLocalDateTimeEntity(this);

    public final vitriol.mvcservice.modules.account.QAccount account;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath introduction = createString("introduction");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final BooleanPath open = createBoolean("open");

    public final ListPath<vitriol.mvcservice.modules.reply.Reply, vitriol.mvcservice.modules.reply.QReply> replies = this.<vitriol.mvcservice.modules.reply.Reply, vitriol.mvcservice.modules.reply.QReply>createList("replies", vitriol.mvcservice.modules.reply.Reply.class, vitriol.mvcservice.modules.reply.QReply.class, PathInits.DIRECT2);

    public final NumberPath<Long> replyCount = createNumber("replyCount", Long.class);

    public final StringPath title = createString("title");

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.account = inits.isInitialized("account") ? new vitriol.mvcservice.modules.account.QAccount(forProperty("account")) : null;
    }

}

