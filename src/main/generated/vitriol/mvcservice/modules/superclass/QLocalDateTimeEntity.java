package vitriol.mvcservice.modules.superclass;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QLocalDateTimeEntity is a Querydsl query type for LocalDateTimeEntity
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QLocalDateTimeEntity extends EntityPathBase<LocalDateTimeEntity> {

    private static final long serialVersionUID = 1978105401L;

    public static final QLocalDateTimeEntity localDateTimeEntity = new QLocalDateTimeEntity("localDateTimeEntity");

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedDate = createDateTime("modifiedDate", java.time.LocalDateTime.class);

    public QLocalDateTimeEntity(String variable) {
        super(LocalDateTimeEntity.class, forVariable(variable));
    }

    public QLocalDateTimeEntity(Path<? extends LocalDateTimeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLocalDateTimeEntity(PathMetadata metadata) {
        super(LocalDateTimeEntity.class, metadata);
    }

}

