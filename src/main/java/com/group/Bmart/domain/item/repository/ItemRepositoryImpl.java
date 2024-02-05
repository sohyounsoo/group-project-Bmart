package com.group.Bmart.domain.item.repository;

import com.group.Bmart.domain.category.MainCategory;
import com.group.Bmart.domain.category.SubCategory;
import com.group.Bmart.domain.item.Item;
import com.group.Bmart.domain.item.ItemSortType;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.group.Bmart.domain.item.QItem.item;
import static com.group.Bmart.domain.item.QLikeItem.likeItem;
import static com.group.Bmart.domain.order.QOrderItem.orderItem;
import static com.group.Bmart.domain.review.QReview.review;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final int NEW_PRODUCT_REFERENCE_TIME = 2;
    private static final double HOT_PRODUCT_REFERENCE_RATE = 3.7;
    private static final int HOT_PRODUCT_REFERENCE_ORDERS = 10;

    @Override
    public List<Item> findNewItemsOrderBy(Long lastIdx, Long lastItemId, ItemSortType sortType,
        Pageable pageable) {
        OrderSpecifier orderSpecifier = createOrderSpecifier(sortType);
        Predicate predicate = item.createdAt.after(
            LocalDateTime.now().minus(NEW_PRODUCT_REFERENCE_TIME, ChronoUnit.WEEKS));

        return queryFactory
            .selectFrom(item)
            .leftJoin(item.reviews, review)
            .leftJoin(item.likeItems, likeItem)
            .leftJoin(item.orderItems, orderItem)
            .where(predicate)
            .groupBy(item)
            .having(
                getHavingCondition(lastIdx, lastItemId, sortType)
            )
            .orderBy(orderSpecifier, item.itemId.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public List<Item> findHotItemsOrderBy(Long lastIdx, Long lastItemId, ItemSortType sortType,
        Pageable pageable) {
        OrderSpecifier orderSpecifier = createOrderSpecifier(sortType);
        Predicate predicate = item.rate.gt(HOT_PRODUCT_REFERENCE_RATE);
        Predicate orderCondition = JPAExpressions.select(orderItem.quantity.sum().coalesce(0))
            .from(orderItem)
            .where(orderItem.item.eq(item))
            .gt(HOT_PRODUCT_REFERENCE_ORDERS);

        return queryFactory
            .selectFrom(item)
            .leftJoin(item.reviews, review)
            .leftJoin(item.likeItems, likeItem)
            .leftJoin(item.orderItems, orderItem)
            .where(predicate)
            .groupBy(item)
            .having(
                getHavingCondition(lastIdx, lastItemId, sortType),
                orderCondition
            )
            .orderBy(orderSpecifier, item.itemId.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public List<Item> findByMainCategoryOrderBy(MainCategory mainCategory, Long lastIdx,
        Long lastItemId, ItemSortType sortType, Pageable pageable) {

        Predicate mainCategoryCondition = item.mainCategory.eq(mainCategory);
        OrderSpecifier orderSpecifier = createOrderSpecifier(sortType);

        return buildDataSetJPAQuery(sortType)
            .where(mainCategoryCondition, getCondition(lastIdx, lastItemId, sortType))
            .groupBy(item.itemId)
            .orderBy(orderSpecifier, item.itemId.desc())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public List<Item> findBySubCategoryOrderBy(MainCategory mainCategory, SubCategory subCategory,
        Long lastIdx, Long lastItemId, ItemSortType sortType, Pageable pageable) {

        Predicate mainCategoryCondition = item.mainCategory.eq(mainCategory);
        Predicate subCategoryCondition = item.subCategory.eq(subCategory);
        OrderSpecifier orderSpecifier = createOrderSpecifier(sortType);

        return buildDataSetJPAQuery(sortType)
            .where(mainCategoryCondition, subCategoryCondition,
                getCondition(lastIdx, lastItemId, sortType))
            .groupBy(item.itemId)
            .orderBy(orderSpecifier, item.itemId.desc())
            .limit(pageable.getPageSize())
            .fetch();
    }

    private JPAQuery<Item> buildDataSetJPAQuery(ItemSortType sortType) {
        JPAQuery<Item> itemJPAQuery = queryFactory.selectFrom(item);
        if (sortType.isPopular()) {
            return itemJPAQuery
                .leftJoin(item.orderItems, orderItem);
        }
        return itemJPAQuery;
    }


    private Predicate getHavingCondition(Long lastIdx, Long lastItemId, ItemSortType sortType) {
        return switch (sortType) {
            case NEW -> item.itemId.lt(lastIdx);
            case HIGHEST_AMOUNT -> item.price.lt(lastIdx)
                .or(item.price.eq(lastIdx.intValue()).and(item.itemId.gt(lastItemId)));
            case LOWEST_AMOUNT -> item.price.gt(lastIdx)
                .or(item.price.eq(lastIdx.intValue()).and(item.itemId.gt(lastItemId)));
            case DISCOUNT -> item.discount.lt(lastIdx)
                .or(item.discount.eq(lastIdx.intValue()).and(item.itemId.gt(lastItemId)));
            default -> JPAExpressions.select(orderItem.quantity.longValue().sum().coalesce(0L))
                .from(orderItem)
                .where(orderItem.item.eq(item))
                .lt(lastIdx);
        };
    }

    private Predicate getCondition(Long lastIdx, Long lastItemId, ItemSortType sortType) {
        return switch (sortType) {
            case NEW -> item.itemId.lt(lastIdx);
            case HIGHEST_AMOUNT -> item.price.lt(lastIdx)
                .or(item.price.eq(lastIdx.intValue()).and(item.itemId.lt(lastItemId)));
            case LOWEST_AMOUNT -> item.price.gt(lastIdx)
                .or(item.price.eq(lastIdx.intValue()).and(item.itemId.lt(lastItemId)));
            case DISCOUNT -> item.discount.lt(lastIdx)
                .or(item.discount.eq(lastIdx.intValue()).and(item.itemId.lt(lastItemId)));
            default -> JPAExpressions.select(orderItem.quantity.longValue().sum().coalesce(0L))
                .from(orderItem)
                .where(orderItem.item.eq(item))
                .lt(lastIdx);
        };
    }

    private OrderSpecifier createOrderSpecifier(ItemSortType sortType) {

        return switch (sortType) {
            case NEW -> new OrderSpecifier<>(Order.DESC, item.itemId);
            case HIGHEST_AMOUNT -> new OrderSpecifier<>(Order.DESC, item.price);
            case LOWEST_AMOUNT -> new OrderSpecifier<>(Order.ASC, item.price);
            case DISCOUNT -> new OrderSpecifier<>(Order.DESC, item.discount);
            default -> new OrderSpecifier<>(Order.DESC, item.orderItems.any().quantity.sum());
        };
    }
}
