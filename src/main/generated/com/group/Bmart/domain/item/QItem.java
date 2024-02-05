package com.group.Bmart.domain.item;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItem is a Querydsl query type for Item
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItem extends EntityPathBase<Item> {

    private static final long serialVersionUID = -1038171338L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItem item = new QItem("item");

    public final com.group.Bmart.global.QBaseTimeEntity _super = new com.group.Bmart.global.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    public final NumberPath<Integer> discount = createNumber("discount", Integer.class);

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final NumberPath<Long> itemId = createNumber("itemId", Long.class);

    public final ListPath<LikeItem, QLikeItem> likeItems = this.<LikeItem, QLikeItem>createList("likeItems", LikeItem.class, QLikeItem.class, PathInits.DIRECT2);

    public final com.group.Bmart.domain.category.QMainCategory mainCategory;

    public final NumberPath<Integer> maxBuyQuantity = createNumber("maxBuyQuantity", Integer.class);

    public final StringPath name = createString("name");

    public final ListPath<com.group.Bmart.domain.order.OrderItem, com.group.Bmart.domain.order.QOrderItem> orderItems = this.<com.group.Bmart.domain.order.OrderItem, com.group.Bmart.domain.order.QOrderItem>createList("orderItems", com.group.Bmart.domain.order.OrderItem.class, com.group.Bmart.domain.order.QOrderItem.class, PathInits.DIRECT2);

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final NumberPath<Double> rate = createNumber("rate", Double.class);

    public final ListPath<com.group.Bmart.domain.review.Review, com.group.Bmart.domain.review.QReview> reviews = this.<com.group.Bmart.domain.review.Review, com.group.Bmart.domain.review.QReview>createList("reviews", com.group.Bmart.domain.review.Review.class, com.group.Bmart.domain.review.QReview.class, PathInits.DIRECT2);

    public final com.group.Bmart.domain.category.QSubCategory subCategory;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QItem(String variable) {
        this(Item.class, forVariable(variable), INITS);
    }

    public QItem(Path<? extends Item> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QItem(PathMetadata metadata, PathInits inits) {
        this(Item.class, metadata, inits);
    }

    public QItem(Class<? extends Item> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.mainCategory = inits.isInitialized("mainCategory") ? new com.group.Bmart.domain.category.QMainCategory(forProperty("mainCategory")) : null;
        this.subCategory = inits.isInitialized("subCategory") ? new com.group.Bmart.domain.category.QSubCategory(forProperty("subCategory"), inits.get("subCategory")) : null;
    }

}

