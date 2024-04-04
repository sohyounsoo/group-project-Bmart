package com.group.Bmart.domain.delivery;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDelivery is a Querydsl query type for Delivery
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDelivery extends EntityPathBase<Delivery> {

    private static final long serialVersionUID = -568712490L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDelivery delivery = new QDelivery("delivery");

    public final com.group.Bmart.global.QBaseTimeEntity _super = new com.group.Bmart.global.QBaseTimeEntity(this);

    public final StringPath address = createString("address");

    public final DateTimePath<java.time.LocalDateTime> arrivedAt = createDateTime("arrivedAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> deliveryFee = createNumber("deliveryFee", Integer.class);

    public final NumberPath<Long> deliveryId = createNumber("deliveryId", Long.class);

    public final EnumPath<DeliveryStatus> deliveryStatus = createEnum("deliveryStatus", DeliveryStatus.class);

    public final com.group.Bmart.domain.order.QOrder order;

    public final NumberPath<Integer> orderPrice = createNumber("orderPrice", Integer.class);

    public final QRider rider;

    public final StringPath riderRequest = createString("riderRequest");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public QDelivery(String variable) {
        this(Delivery.class, forVariable(variable), INITS);
    }

    public QDelivery(Path<? extends Delivery> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDelivery(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDelivery(PathMetadata metadata, PathInits inits) {
        this(Delivery.class, metadata, inits);
    }

    public QDelivery(Class<? extends Delivery> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.order = inits.isInitialized("order") ? new com.group.Bmart.domain.order.QOrder(forProperty("order"), inits.get("order")) : null;
        this.rider = inits.isInitialized("rider") ? new QRider(forProperty("rider")) : null;
    }

}

